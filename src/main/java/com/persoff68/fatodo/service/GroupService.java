package com.persoff68.fatodo.service;

import com.persoff68.fatodo.client.CommentServiceClient;
import com.persoff68.fatodo.model.Configuration;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.security.exception.UnauthorizedException;
import com.persoff68.fatodo.security.util.SecurityUtils;
import com.persoff68.fatodo.service.exception.ModelAlreadyExistsException;
import com.persoff68.fatodo.service.exception.ModelInvalidException;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import com.persoff68.fatodo.service.validator.GroupValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final ConfigurationService configurationService;
    private final ImageService imageService;
    private final ItemService itemService;
    private final PermissionService permissionService;
    private final GroupValidator groupValidator;
    private final CommentServiceClient commentServiceClient;

    public List<Group> getAllByUserId(UUID userId) {
        Map<UUID, Integer> orderMap = configurationService.getByUserId(userId).getGroups()
                .stream().collect(Collectors.toMap(Configuration.Group::getId, Configuration.Group::getOrder));
        List<Group> groupList = groupRepository.findAllByUserId(userId);
        return groupList.stream()
                .sorted(Comparator.comparingInt(g -> orderMap.getOrDefault(g.getId(), Integer.MAX_VALUE)))
                .toList();
    }

    public Group getByIdWithoutPermissionCheck(UUID id) {
        return groupRepository.findById(id)
                .orElseThrow(ModelNotFoundException::new);
    }

    public Group getById(UUID id) {
        permissionService.checkReadPermission(id);
        return groupRepository.findById(id)
                .orElseThrow(ModelNotFoundException::new);
    }

    public Group create(Group groupToCreate, byte[] image) {
        if (groupToCreate.getId() != null) {
            throw new ModelAlreadyExistsException();
        }

        groupToCreate.setMembers(createInitMemberList());
        groupValidator.validateCreate(groupToCreate);
        Group group = groupRepository.save(groupToCreate);
        configurationService.addGroup(group);

        if (image != null && image.length > 0) {
            group.setImageFilename(null);
            String imageFilename = imageService.createGroup(image);
            group.setImageFilename(imageFilename);
            group = groupRepository.save(group);
        }

        return group;
    }

    public Group update(Group groupToUpdate, byte[] image) {
        UUID id = groupToUpdate.getId();
        if (id == null) {
            throw new ModelInvalidException();
        }
        permissionService.checkAdminPermission(id);

        Group group = groupRepository.findById(id)
                .orElseThrow(ModelNotFoundException::new);
        group.setTitle(groupToUpdate.getTitle());
        group.setColor(groupToUpdate.getColor());
        groupValidator.validateUpdate(group);
        group = groupRepository.save(group);

        if (image != null && image.length > 0) {
            group.setImageFilename(null);
            String imageFilename = imageService.updateGroup(group, groupToUpdate, image);
            group.setImageFilename(imageFilename);
            group = groupRepository.save(group);
        }

        return group;
    }

    public void delete(UUID groupId) {
        permissionService.checkAdminPermission(groupId);

        Group group = groupRepository.findById(groupId)
                .orElseThrow(ModelNotFoundException::new);

        List<UUID> idList = Collections.singletonList(groupId);
        commentServiceClient.deleteAllThreadsByTargetIds(idList);
        itemService.deleteAllByGroupId(group.getId());
        imageService.deleteGroup(group);
        configurationService.deleteGroup(group);
        groupRepository.delete(group);
    }


    private List<Member> createInitMemberList() {
        UUID userId = SecurityUtils.getCurrentId()
                .orElseThrow(UnauthorizedException::new);
        Member member = Member.adminMember(userId);
        return Collections.singletonList(member);
    }

}
