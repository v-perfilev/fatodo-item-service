package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.repository.ItemRepository;
import com.persoff68.fatodo.service.client.ContactService;
import com.persoff68.fatodo.service.client.EventService;
import com.persoff68.fatodo.service.client.PermissionService;
import com.persoff68.fatodo.service.client.WsService;
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

    private final PermissionService permissionService;
    private final EventService eventService;
    private final WsService wsService;
    private final ContactService contactService;
    private final GroupValidator groupValidator;
    private final GroupRepository groupRepository;
    private final ItemRepository itemRepository;

    public List<UUID> getUserIdsByGroupId(UUID userId, UUID groupId) {
        permissionService.checkGroupPermission(userId, Permission.READ, groupId);
        Group group = groupRepository.findById(groupId).orElseThrow(ModelNotFoundException::new);
        return group.getMembers().stream().map(Member::getUserId).toList();
    }


    public List<UUID> getUserIdsByItemId(UUID userId, UUID itemId) {
        permissionService.checkItemPermission(userId, Permission.READ, itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(ModelNotFoundException::new);
        return getUserIdsByGroupId(userId, item.getGroup().getId());
    }


    public void addMembersToGroup(UUID userId, UUID groupId, List<UUID> userIdList) {
        permissionService.checkGroupPermission(userId, Permission.ADMIN, groupId);
        contactService.checkIfUsersInContactList(userIdList);
        Group group = groupRepository.findById(groupId).orElseThrow(ModelNotFoundException::new);

        List<Member> memberList = group.getMembers();
        List<UUID> memberIdList = memberList.stream().map(Member::getUserId).toList();
        List<Member> newMemberList = userIdList.stream()
                .filter(id -> !memberIdList.contains(id)).map(id -> Member.readMember(group, id))
                .toList();
        memberList.addAll(newMemberList);

        groupValidator.validateUpdate(group);
        Group savedGroup = groupRepository.save(group);

        // EVENT
        eventService.sendMemberAddEvent(savedGroup, newMemberList, userId);
        // WS
        wsService.sendMemberAddEvent(savedGroup, newMemberList, userId);
    }


    public void removeMembersFromGroup(UUID userId, UUID groupId, List<UUID> userIdList) {
        permissionService.checkGroupPermission(userId, Permission.ADMIN, groupId);
        Group group = groupRepository.findById(groupId).orElseThrow(ModelNotFoundException::new);

        List<Member> memberList = group.getMembers();
        List<Member> memberToDeleteList = memberList.stream()
                .filter(member -> userIdList.contains(member.getUserId()))
                .toList();
        memberList.removeAll(memberToDeleteList);

        groupValidator.validateUpdate(group);
        Group savedGroup = groupRepository.save(group);

        // EVENT
        eventService.sendMemberDeleteEvent(savedGroup, memberToDeleteList, userId);
        // WS
        wsService.sendMemberDeleteEvent(savedGroup, memberToDeleteList, userId);
    }


    public void editGroupMember(UUID userId, UUID groupId, UUID editedUserId, Permission permission) {
        permissionService.checkGroupPermission(userId, Permission.ADMIN, groupId);
        Group group = groupRepository.findById(groupId).orElseThrow(ModelNotFoundException::new);

        List<Member> memberList = group.getMembers();
        Member member = memberList.stream()
                .filter(m -> m.getUserId().equals(editedUserId))
                .findFirst()
                .orElseThrow(ModelNotFoundException::new);
        member.setPermission(permission);

        groupValidator.validateUpdate(group);
        Group savedGroup = groupRepository.save(group);

        // EVENT
        eventService.sendMemberRoleEvent(savedGroup, member, userId);
        // WS
        wsService.sendMemberRoleEvent(savedGroup, member, userId);
    }

    public void leaveGroup(UUID userId, UUID groupId) {
        permissionService.checkGroupPermission(userId, Permission.READ, groupId);
        Group group = groupRepository.findById(groupId).orElseThrow(ModelNotFoundException::new);

        List<Member> memberList = group.getMembers();
        Member memberToDelete = memberList.stream()
                .filter(member -> member.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(ModelNotFoundException::new);
        memberList.remove(memberToDelete);

        groupValidator.validateUpdate(group);
        Group savedGroup = groupRepository.save(group);

        // EVENT
        eventService.sendMemberLeaveEvent(savedGroup, memberToDelete, userId);
        // WS
        wsService.sendMemberLeaveEvent(savedGroup, memberToDelete, userId);
    }

}
