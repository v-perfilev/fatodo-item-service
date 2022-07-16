package com.persoff68.fatodo.service.client;

import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.model.dto.CreateItemEventDTO;
import com.persoff68.fatodo.model.dto.DeleteEventsDTO;
import com.persoff68.fatodo.model.dto.DeleteUserEventsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventServiceClient eventServiceClient;

    public void sendItemCreateEvent(Item item) {
        List<UUID> recipientIdList = item.getGroup().getMembers().stream().map(Member::getUserId).toList();
        UUID userId = item.getCreatedBy();
        UUID groupId = item.getGroup().getId();
        UUID itemId = item.getId();
        CreateItemEventDTO dto = CreateItemEventDTO.itemCreate(recipientIdList, userId, groupId, itemId);
        eventServiceClient.addItemEvent(dto);
    }

    public void sendItemUpdateEvent(Item item) {
        List<UUID> recipientIdList = item.getGroup().getMembers().stream().map(Member::getUserId).toList();
        UUID userId = item.getCreatedBy();
        UUID groupId = item.getGroup().getId();
        UUID itemId = item.getId();
        CreateItemEventDTO dto = CreateItemEventDTO.itemUpdate(recipientIdList, userId, groupId, itemId);
        eventServiceClient.addItemEvent(dto);
    }

    public void sendGroupCreateEvent(Group group) {
        List<UUID> recipientIdList = group.getMembers().stream().map(Member::getUserId).toList();
        UUID userId = group.getCreatedBy();
        UUID groupId = group.getId();
        CreateItemEventDTO dto = CreateItemEventDTO.groupCreate(recipientIdList, userId, groupId);
        eventServiceClient.addItemEvent(dto);
    }

    public void sendGroupUpdateEvent(Group group) {
        List<UUID> recipientIdList = group.getMembers().stream().map(Member::getUserId).toList();
        UUID userId = group.getCreatedBy();
        UUID groupId = group.getId();
        CreateItemEventDTO dto = CreateItemEventDTO.groupUpdate(recipientIdList, userId, groupId);
        eventServiceClient.addItemEvent(dto);
    }

    public void sendMemberAddEvent(Group group, UUID userId, List<Member> memberList) {
        List<UUID> recipientIdList = group.getMembers().stream().map(Member::getUserId).toList();
        UUID groupId = group.getId();
        List<UUID> userIdList = memberList.stream().map(Member::getUserId).toList();
        CreateItemEventDTO dto = CreateItemEventDTO.memberAdd(recipientIdList, userId, groupId, userIdList);
        eventServiceClient.addItemEvent(dto);
    }

    public void sendMemberDeleteEvent(Group group, UUID userId, List<Member> memberList) {
        List<UUID> recipientIdList = group.getMembers().stream().map(Member::getUserId).toList();
        UUID groupId = group.getId();
        List<UUID> userIdList = memberList.stream().map(Member::getUserId).toList();
        CreateItemEventDTO dto = CreateItemEventDTO.memberDelete(recipientIdList, userId, groupId, userIdList);
        eventServiceClient.addItemEvent(dto);
    }

    public void sendMemberLeaveEvent(Group group, UUID userId) {
        List<UUID> recipientIdList = group.getMembers().stream().map(Member::getUserId).toList();
        UUID groupId = group.getId();
        CreateItemEventDTO dto = CreateItemEventDTO.memberLeave(recipientIdList, userId, groupId);
        eventServiceClient.addItemEvent(dto);
    }

    public void sendMemberRoleEvent(Group group, UUID userId, UUID editedUserId, Permission permission) {
        List<UUID> recipientIdList = group.getMembers().stream().map(Member::getUserId).toList();
        UUID groupId = group.getId();
        List<UUID> userIdList = Collections.singletonList(editedUserId);
        String role = permission.name();
        CreateItemEventDTO dto = CreateItemEventDTO.memberRole(recipientIdList, userId, groupId, userIdList, role);
        eventServiceClient.addItemEvent(dto);
    }

    public void deleteGroupEvents(UUID groupId) {
        DeleteEventsDTO dto = new DeleteEventsDTO(groupId);
        eventServiceClient.deleteGroupEvents(dto);
    }

    public void deleteItemEvents(UUID itemId) {
        DeleteEventsDTO dto = new DeleteEventsDTO(itemId);
        eventServiceClient.deleteItemEvents(dto);
    }

    public void deleteGroupEventsForUser(UUID groupId, List<UUID> userIdList) {
        DeleteUserEventsDTO dto = new DeleteUserEventsDTO(groupId, userIdList);
        eventServiceClient.deleteGroupEventsForUsers(dto);
    }

}
