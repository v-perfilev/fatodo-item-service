package com.persoff68.fatodo.service.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.mapper.GroupMapper;
import com.persoff68.fatodo.mapper.ItemMapper;
import com.persoff68.fatodo.mapper.MemberMapper;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.constant.EventType;
import com.persoff68.fatodo.model.dto.GroupDTO;
import com.persoff68.fatodo.model.dto.ItemDTO;
import com.persoff68.fatodo.model.dto.MemberDTO;
import com.persoff68.fatodo.model.dto.event.EventDTO;
import com.persoff68.fatodo.service.exception.ModelInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EventService {

    private final EventServiceClient eventServiceClient;
    private final GroupMapper groupMapper;
    private final ItemMapper itemMapper;
    private final MemberMapper memberMapper;
    private final ObjectMapper objectMapper;

    public void sendGroupCreateEvent(Group group, UUID userId) {
        List<UUID> userIdList = group.getMembers().stream().map(Member::getUserId).toList();
        GroupDTO groupDTO = groupMapper.pojoToDTO(group);
        String payload = serialize(groupDTO);
        EventDTO dto = new EventDTO(userIdList, EventType.ITEM_GROUP_CREATE, payload, userId);
        eventServiceClient.addEvent(dto);
    }

    public void sendGroupUpdateEvent(Group group, UUID userId) {
        List<UUID> userIdList = group.getMembers().stream().map(Member::getUserId).toList();
        GroupDTO groupDTO = groupMapper.pojoToDTO(group);
        String payload = serialize(groupDTO);
        EventDTO dto = new EventDTO(userIdList, EventType.ITEM_GROUP_UPDATE, payload, userId);
        eventServiceClient.addEvent(dto);
    }

    public void sendGroupDeleteEvent(Group group, UUID userId) {
        List<UUID> userIdList = group.getMembers().stream().map(Member::getUserId).toList();
        GroupDTO groupDTO = groupMapper.pojoToDTO(group);
        String payload = serialize(groupDTO);
        EventDTO dto = new EventDTO(userIdList, EventType.ITEM_GROUP_DELETE, payload, userId);
        eventServiceClient.addEvent(dto);
    }

    public void sendItemCreateEvent(Item item, UUID userId) {
        List<UUID> userIdList = item.getGroup().getMembers().stream().map(Member::getUserId).toList();
        ItemDTO itemDTO = itemMapper.pojoToDTO(item);
        String payload = serialize(itemDTO);
        EventDTO dto = new EventDTO(userIdList, EventType.ITEM_CREATE, payload, userId);
        eventServiceClient.addEvent(dto);
    }

    public void sendItemUpdateEvent(Item item, UUID userId) {
        List<UUID> userIdList = item.getGroup().getMembers().stream().map(Member::getUserId).toList();
        ItemDTO itemDTO = itemMapper.pojoToDTO(item);
        String payload = serialize(itemDTO);
        EventDTO dto = new EventDTO(userIdList, EventType.ITEM_UPDATE, payload, userId);
        eventServiceClient.addEvent(dto);
    }

    public void sendItemUpdateStatusEvent(Item item, UUID userId) {
        List<UUID> userIdList = item.getGroup().getMembers().stream().map(Member::getUserId).toList();
        ItemDTO itemDTO = itemMapper.pojoToDTO(item);
        String payload = serialize(itemDTO);
        EventDTO dto = new EventDTO(userIdList, EventType.ITEM_UPDATE_STATUS, payload, userId);
        eventServiceClient.addEvent(dto);
    }

    public void sendItemUpdateArchivedEvent(Item item, UUID userId) {
        List<UUID> userIdList = item.getGroup().getMembers().stream().map(Member::getUserId).toList();
        ItemDTO itemDTO = itemMapper.pojoToDTO(item);
        String payload = serialize(itemDTO);
        EventDTO dto = new EventDTO(userIdList, EventType.ITEM_UPDATE_ARCHIVED, payload, userId);
        eventServiceClient.addEvent(dto);
    }

    public void sendItemDeleteEvent(Item item, UUID userId) {
        List<UUID> userIdList = item.getGroup().getMembers().stream().map(Member::getUserId).toList();
        ItemDTO itemDTO = itemMapper.pojoToDTO(item);
        String payload = serialize(itemDTO);
        EventDTO dto = new EventDTO(userIdList, EventType.ITEM_DELETE, payload, userId);
        eventServiceClient.addEvent(dto);
    }

    public void sendMemberAddEvent(Group group, List<Member> memberList, UUID userId) {
        List<UUID> userIdList = group.getMembers().stream().map(Member::getUserId).toList();
        List<MemberDTO> memberDTOList = memberList.stream().map(memberMapper::pojoToDTO).toList();
        String payload = serialize(memberDTOList);
        EventDTO dto = new EventDTO(userIdList, EventType.ITEM_MEMBER_ADD, payload, userId);
        eventServiceClient.addEvent(dto);
    }

    public void sendMemberDeleteEvent(Group group, List<Member> memberList, UUID userId) {
        List<UUID> userIdList = group.getMembers().stream().map(Member::getUserId).toList();
        List<MemberDTO> memberDTOList = memberList.stream().map(memberMapper::pojoToDTO).toList();
        String payload = serialize(memberDTOList);
        EventDTO dto = new EventDTO(userIdList, EventType.ITEM_MEMBER_DELETE, payload, userId);
        eventServiceClient.addEvent(dto);
    }

    public void sendMemberLeaveEvent(Group group, Member member, UUID userId) {
        List<UUID> userIdList = group.getMembers().stream().map(Member::getUserId).toList();
        MemberDTO memberDTO = memberMapper.pojoToDTO(member);
        String payload = serialize(memberDTO);
        EventDTO dto = new EventDTO(userIdList, EventType.ITEM_MEMBER_LEAVE, payload, userId);
        eventServiceClient.addEvent(dto);
    }

    public void sendMemberRoleEvent(Group group, Member member, UUID userId) {
        List<UUID> userIdList = group.getMembers().stream().map(Member::getUserId).toList();
        MemberDTO memberDTO = memberMapper.pojoToDTO(member);
        String payload = serialize(memberDTO);
        EventDTO dto = new EventDTO(userIdList, EventType.ITEM_MEMBER_ROLE, payload, userId);
        eventServiceClient.addEvent(dto);
    }

    private String serialize(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new ModelInvalidException();
        }
    }

}
