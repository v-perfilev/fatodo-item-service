package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.repository.ItemRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import com.persoff68.fatodo.service.validator.GroupValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final GroupRepository groupRepository;
    private final ItemRepository itemRepository;
    private final PermissionService permissionService;
    private final ContactService contactService;
    private final GroupValidator groupValidator;


    public List<UUID> getUserIdsByGroupId(UUID groupId) {
        permissionService.checkReadPermission(groupId);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(ModelNotFoundException::new);
        return group.getMembers().stream()
                .map(Member::getUserId)
                .toList();
    }


    public List<UUID> getUserIdsByItemId(UUID itemId) {
        permissionService.checkReadItemPermission(itemId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(ModelNotFoundException::new);
        return getUserIdsByGroupId(item.getGroup().getId());
    }


    public void addMembersToGroup(UUID groupId, List<UUID> userIdList) {
        permissionService.checkAdminPermission(groupId);
        contactService.checkIfUsersInContactList(userIdList);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(ModelNotFoundException::new);

        List<Member> memberList = group.getMembers();
        List<UUID> memberIdList = memberList.stream()
                .map(Member::getUserId)
                .toList();
        List<Member> newMemberList = userIdList.stream()
                .filter(userId -> !memberIdList.contains(userId))
                .map(id -> Member.readMember(group, id))
                .toList();
        memberList.addAll(newMemberList);

        groupValidator.validateUpdate(group);
        groupRepository.save(group);
    }


    public void removeMembersFromGroup(UUID groupId, List<UUID> userIdList) {
        permissionService.checkAdminPermission(groupId);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(ModelNotFoundException::new);

        List<Member> memberList = group.getMembers();
        List<Member> memberToDeleteList = memberList.stream()
                .filter(member -> userIdList.contains(member.getUserId()))
                .toList();
        memberList.removeAll(memberToDeleteList);

        groupValidator.validateUpdate(group);
        groupRepository.save(group);
    }


    public void editGroupMember(UUID groupId, UUID userId, Permission permission) {
        permissionService.checkAdminPermission(groupId);
        Group group = groupRepository.findById(groupId)
                .orElseThrow(ModelNotFoundException::new);

        List<Member> memberList = group.getMembers();
        Member member = memberList.stream()
                .filter(m -> m.getUserId().equals(userId))
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
        List<Member> memberToDeleteList = memberList.stream()
                .filter(member -> member.getUserId().equals(userId))
                .toList();
        memberList.removeAll(memberToDeleteList);

        groupValidator.validateUpdate(group);
        groupRepository.save(group);
    }

}
