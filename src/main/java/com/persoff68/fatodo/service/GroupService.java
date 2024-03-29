package com.persoff68.fatodo.service;

import com.persoff68.fatodo.client.CommentServiceClient;
import com.persoff68.fatodo.client.NotificationServiceClient;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.PageableList;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.service.client.EventService;
import com.persoff68.fatodo.service.client.ImageService;
import com.persoff68.fatodo.service.client.PermissionService;
import com.persoff68.fatodo.service.client.WsService;
import com.persoff68.fatodo.service.exception.ModelAlreadyExistsException;
import com.persoff68.fatodo.service.exception.ModelInvalidException;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import com.persoff68.fatodo.service.validator.GroupValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final ConfigurationService configurationService;
    private final ImageService imageService;
    private final PermissionService permissionService;
    private final EventService eventService;
    private final WsService wsService;
    private final GroupValidator groupValidator;
    private final GroupRepository groupRepository;
    private final CommentServiceClient commentServiceClient;
    private final NotificationServiceClient notificationServiceClient;

    public List<UUID> getAllIdsByUserId(UUID userId) {
        List<Group> groupList = groupRepository.findAllByUserId(userId);
        return groupList.stream()
                .map(Group::getId)
                .toList();
    }

    public PageableList<Group> getAllByUserId(UUID userId, Pageable pageable) {
        Map<UUID, Integer> orderMap = configurationService.getByUserId(userId).getOrderMap();
        List<Group> groupList = groupRepository.findAllByUserId(userId);
        List<Group> sortedGroupList = groupList.stream()
                .sorted(Comparator.comparingInt(g -> orderMap.getOrDefault(g.getId(), Integer.MAX_VALUE)))
                .toList();
        List<Group> slicedGroupList = pageable.getOffset() > sortedGroupList.size()
                ? Collections.emptyList()
                : sortedGroupList.subList((int) pageable.getOffset(),
                Math.min(sortedGroupList.size(), (int) pageable.getOffset() + pageable.getPageSize()));
        return PageableList.of(slicedGroupList, sortedGroupList.size());
    }

    public List<Group> getAllCommonByUserIds(UUID firstUserId, UUID secondUserId) {
        Map<UUID, Integer> orderMap = configurationService.getByUserId(firstUserId).getOrderMap();
        List<Group> firstUserGroupList = groupRepository.findAllByUserId(firstUserId);
        List<Group> secondUserGroupList = groupRepository.findAllByUserId(secondUserId);
        List<UUID> secondUserGroupIdList = secondUserGroupList.stream()
                .map(Group::getId)
                .toList();
        List<Group> commonGroupList = firstUserGroupList.stream()
                .filter(g -> secondUserGroupIdList.contains(g.getId()))
                .toList();
        return commonGroupList.stream()
                .sorted(Comparator.comparingInt(g -> orderMap.getOrDefault(g.getId(), Integer.MAX_VALUE)))
                .toList();
    }

    @Transactional
    public List<Group> getAllByIds(UUID userId, List<UUID> groupIdList) {
        List<Group> groupList = groupRepository.findAllById(groupIdList);
        return groupList.stream()
                .filter(g -> g.getMembers().stream().map(Member::getUserId).anyMatch(userId::equals))
                .toList();
    }

    public Group getByIdWithoutPermissionCheck(UUID groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(ModelNotFoundException::new);
    }

    public Group getById(UUID userId, UUID groupId) {
        permissionService.checkGroupPermission(userId, Permission.READ, groupId);
        return groupRepository.findById(groupId)
                .orElseThrow(ModelNotFoundException::new);
    }

    @Transactional
    public Group create(UUID userId, Group groupToCreate, byte[] image) {
        if (groupToCreate.getId() != null) {
            throw new ModelAlreadyExistsException();
        }

        List<Member> memberList = createInitMemberList(groupToCreate, userId);
        groupToCreate.setMembers(memberList);
        groupToCreate.setImageFilename(null);
        groupValidator.validateCreate(groupToCreate);
        Group group = groupRepository.save(groupToCreate);
        configurationService.addGroup(group);

        if (image != null && image.length > 0) {
            String imageFilename = imageService.createGroup(image);
            group.setImageFilename(imageFilename);
        }

        // EVENT
        eventService.sendGroupCreateEvent(group, userId);
        // WS
        wsService.sendGroupCreateEvent(group, userId);

        return group;
    }

    @Transactional
    public Group update(UUID userId, Group groupToUpdate, byte[] image) {
        UUID groupId = groupToUpdate.getId();
        if (groupId == null) {
            throw new ModelInvalidException();
        }
        permissionService.checkGroupPermission(userId, Permission.ADMIN, groupId);

        Group group = groupRepository.findById(groupId)
                .orElseThrow(ModelNotFoundException::new);
        group.setTitle(groupToUpdate.getTitle());
        group.setColor(groupToUpdate.getColor());
        groupValidator.validateUpdate(group);

        String imageFilename = imageService.updateGroup(group, groupToUpdate, image);
        group.setImageFilename(imageFilename);

        // EVENT
        eventService.sendGroupUpdateEvent(group, userId);
        // WS
        wsService.sendGroupUpdateEvent(group, userId);

        return groupRepository.save(group);
    }

    @Transactional
    public void delete(UUID userId, UUID groupId) {
        permissionService.checkGroupPermission(userId, Permission.ADMIN, groupId);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(ModelNotFoundException::new);

        commentServiceClient.deleteAllThreadsByParentId(groupId);
        notificationServiceClient.deleteRemindersByParentId(groupId);

        imageService.deleteGroup(group);
        configurationService.deleteGroup(group);

        // EVENT
        eventService.sendGroupDeleteEvent(group, userId);
        // WS
        wsService.sendGroupDeleteEvent(group, userId);

        group.setDeleted(true);
        group.getItems().forEach(item -> item.setDeleted(true));
        groupRepository.save(group);
    }


    private List<Member> createInitMemberList(Group group, UUID userId) {
        Member member = Member.adminMember(group, userId);
        return Collections.singletonList(member);
    }

}
