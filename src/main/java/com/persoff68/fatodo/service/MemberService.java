package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.repository.ItemRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import com.persoff68.fatodo.service.validator.GroupValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final GroupRepository groupRepository;
    private final ItemRepository itemRepository;
    private final PermissionService permissionService;
    private final UserService userService;
    private final GroupValidator groupValidator;

    public void addMembersToGroup(UUID groupId, List<UUID> userIdList) {
        permissionService.checkAdminPermission(groupId);
        userService.checkUsersExist(userIdList);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(ModelNotFoundException::new);

        List<Member> oldMemberList = group.getMembers();
        List<UUID> oldUserIdList = oldMemberList.stream()
                .map(Member::getId)
                .collect(Collectors.toList());
        List<UUID> newUserIdList = userIdList.stream()
                .filter(userId -> !oldUserIdList.contains(userId))
                .collect(Collectors.toList());
        List<Member> newMemberList = newUserIdList.stream()
                .map(Member::readMember)
                .collect(Collectors.toList());
        List<Member> updatedMemberList = Stream.concat(oldMemberList.stream(), newMemberList.stream())
                .collect(Collectors.toList());

        group.setMembers(updatedMemberList);
        groupValidator.validateUpdate(group);
        groupRepository.save(group);
    }

    public void removeMembersFromGroup(UUID groupId, List<UUID> userIdList) {
        permissionService.checkAdminPermission(groupId);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(ModelNotFoundException::new);

        List<Member> memberList = group.getMembers();
        List<Member> updatedMemberList = memberList.stream()
                .filter(member -> !userIdList.contains(member.getId()))
                .collect(Collectors.toList());

        group.setMembers(updatedMemberList);
        groupValidator.validateUpdate(group);
        groupRepository.save(group);
    }

    public void editGroupMember(UUID groupId, UUID userId, Permission permission) {
        permissionService.checkAdminPermission(groupId);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(ModelNotFoundException::new);

        List<Member> memberList = group.getMembers();
        Member member = memberList.stream()
                .filter(m -> m.getId().equals(userId))
                .findFirst()
                .orElseThrow(ModelNotFoundException::new);
        member.setPermission(permission);

        groupValidator.validateUpdate(group);
        groupRepository.save(group);
    }

    public void leaveGroup(UUID groupId, UUID userId) {
        permissionService.checkReadPermission(groupId);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(ModelNotFoundException::new);

        List<Member> memberList = group.getMembers();
        List<Member> updatedMemberList = memberList.stream()
                .filter(member -> !member.getId().equals(userId))
                .collect(Collectors.toList());

        group.setMembers(updatedMemberList);
        groupValidator.validateUpdate(group);
        groupRepository.save(group);
    }

}
