package com.persoff68.fatodo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateItemEventDTO {

    private EventType type;

    private List<UUID> recipientIds;

    private UUID userId;

    private UUID groupId;

    private UUID itemId;

    private List<UUID> userIds;

    private String role;

    public enum EventType {
        ITEM_CREATE,
        ITEM_UPDATE,
        ITEM_GROUP_CREATE,
        ITEM_GROUP_UPDATE,
        ITEM_MEMBER_ADD,
        ITEM_MEMBER_DELETE,
        ITEM_MEMBER_LEAVE,
        ITEM_MEMBER_ROLE,
    }

    public static CreateItemEventDTO itemCreate(List<UUID> recipientIds, UUID userId, UUID groupId, UUID itemId) {
        CreateItemEventDTO dto = new CreateItemEventDTO();
        dto.setType(EventType.ITEM_CREATE);
        dto.setRecipientIds(recipientIds);
        dto.setUserId(userId);
        dto.setGroupId(groupId);
        dto.setItemId(itemId);
        return dto;
    }

    public static CreateItemEventDTO itemUpdate(List<UUID> recipientIds, UUID userId, UUID groupId, UUID itemId) {
        CreateItemEventDTO dto = new CreateItemEventDTO();
        dto.setType(EventType.ITEM_UPDATE);
        dto.setRecipientIds(recipientIds);
        dto.setUserId(userId);
        dto.setGroupId(groupId);
        dto.setItemId(itemId);
        return dto;
    }

    public static CreateItemEventDTO groupCreate(List<UUID> recipientIds, UUID userId, UUID groupId) {
        CreateItemEventDTO dto = new CreateItemEventDTO();
        dto.setType(EventType.ITEM_GROUP_CREATE);
        dto.setRecipientIds(recipientIds);
        dto.setUserId(userId);
        dto.setGroupId(groupId);
        return dto;
    }

    public static CreateItemEventDTO groupUpdate(List<UUID> recipientIds, UUID userId, UUID groupId) {
        CreateItemEventDTO dto = new CreateItemEventDTO();
        dto.setType(EventType.ITEM_GROUP_UPDATE);
        dto.setRecipientIds(recipientIds);
        dto.setUserId(userId);
        dto.setGroupId(groupId);
        return dto;
    }

    public static CreateItemEventDTO memberAdd(List<UUID> recipientIds, UUID userId, UUID groupId,
                                               List<UUID> userIds) {
        CreateItemEventDTO dto = new CreateItemEventDTO();
        dto.setType(EventType.ITEM_MEMBER_ADD);
        dto.setRecipientIds(recipientIds);
        dto.setUserId(userId);
        dto.setGroupId(groupId);
        dto.setUserIds(userIds);
        return dto;
    }

    public static CreateItemEventDTO memberDelete(List<UUID> recipientIds, UUID userId, UUID groupId,
                                                  List<UUID> userIds) {
        CreateItemEventDTO dto = new CreateItemEventDTO();
        dto.setType(EventType.ITEM_MEMBER_DELETE);
        dto.setRecipientIds(recipientIds);
        dto.setUserId(userId);
        dto.setGroupId(groupId);
        dto.setUserIds(userIds);
        return dto;
    }

    public static CreateItemEventDTO memberLeave(List<UUID> recipientIds, UUID userId, UUID groupId) {
        CreateItemEventDTO dto = new CreateItemEventDTO();
        dto.setType(EventType.ITEM_MEMBER_LEAVE);
        dto.setRecipientIds(recipientIds);
        dto.setUserId(userId);
        dto.setGroupId(groupId);
        return dto;
    }

    public static CreateItemEventDTO memberRole(List<UUID> recipientIds, UUID userId, UUID groupId,
                                                List<UUID> userIds, String role) {
        CreateItemEventDTO dto = new CreateItemEventDTO();
        dto.setType(EventType.ITEM_MEMBER_LEAVE);
        dto.setRecipientIds(recipientIds);
        dto.setUserId(userId);
        dto.setGroupId(groupId);
        dto.setUserIds(userIds);
        dto.setRole(role);
        return dto;
    }

}
