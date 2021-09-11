package com.persoff68.fatodo.service;

import com.persoff68.fatodo.config.aop.cache.annotation.CacheableMethod;
import com.persoff68.fatodo.model.Configuration;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.constant.Permission;
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

    @CacheableMethod(cacheName = "groups-by-user-id", key = "#userId")
    public List<Group> getAllByUserId(UUID userId) {
        Map<UUID, Integer> orderMap = configurationService.getByUserId(userId).getGroups()
                .stream().collect(Collectors.toMap(Configuration.Group::getId, Configuration.Group::getOrder));
        List<Group> groupList = groupRepository.findAllByUserId(userId);
        return groupList.stream()
                .sorted(Comparator.comparingInt(g -> orderMap.getOrDefault(g.getId(), Integer.MAX_VALUE)))
                .collect(Collectors.toList());
    }

    @CacheableMethod(cacheName = "groups-by-id", key = "#id")
    public Group getById(UUID id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.checkReadPermission(group.getId());
        return group;
    }

    public Group create(Group newGroup, byte[] image) {
        if (newGroup.getId() != null) {
            throw new ModelAlreadyExistsException();
        }

        newGroup.setMembers(createInitMemberList());
        groupValidator.validateCreate(newGroup);

        String imageFilename = imageService.createGroup(image);
        newGroup.setImageFilename(imageFilename);
        Group group = groupRepository.save(newGroup);
        configurationService.addGroup(group);
        return group;
    }

    public Group update(Group newGroup, byte[] image) {
        if (newGroup.getId() == null) {
            throw new ModelInvalidException();
        }
        Group group = groupRepository.findById(newGroup.getId())
                .orElseThrow(ModelNotFoundException::new);

        permissionService.checkEditPermission(group.getId());
        group.setTitle(newGroup.getTitle());
        group.setColor(newGroup.getColor());
        groupValidator.validateUpdate(group);

        String imageFilename = imageService.updateGroup(group, newGroup, image);
        group.setImageFilename(imageFilename);
        return groupRepository.save(group);
    }

    public void delete(UUID id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(ModelNotFoundException::new);

        permissionService.checkAdminPermission(group.getId());
        groupValidator.validateDelete(group);

        itemService.deleteAllByGroupId(group.getId());
        imageService.deleteGroup(group);
        groupRepository.delete(group);
        configurationService.removeGroup(group);
    }


    private List<Member> createInitMemberList() {
        UUID userId = SecurityUtils.getCurrentId()
                .orElseThrow(UnauthorizedException::new);
        Member member = new Member();
        member.setId(userId);
        member.setPermission(Permission.ADMIN);
        return Collections.singletonList(member);
    }

}
