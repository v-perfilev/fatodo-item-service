package com.persoff68.fatodo.service.client;

import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.mapper.GroupMapper;
import com.persoff68.fatodo.mapper.ItemMapper;
import com.persoff68.fatodo.mapper.MemberMapper;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.constant.EventType;
import com.persoff68.fatodo.model.dto.EventDTO;
import com.persoff68.fatodo.model.dto.GroupDTO;
import com.persoff68.fatodo.model.dto.ItemDTO;
import com.persoff68.fatodo.model.dto.MemberDTO;
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

    public void sendGroupCreateEvent(Group group) {
        List<UUID> userIdList = group.getMembers().stream().map(Member::getUserId).toList();
        GroupDTO groupDTO = groupMapper.pojoToDTO(group);
        EventDTO dto = new EventDTO(userIdList, EventType.ITEM_GROUP_CREATE, groupDTO, group.getCreatedBy());
        eventServiceClient.addEvent(dto);
    }

    public void sendGroupUpdateEvent(Group group) {
        List<UUID> userIdList = group.getMembers().stream().map(Member::getUserId).toList();
        GroupDTO groupDTO = groupMapper.pojoToDTO(group);
        EventDTO dto = new EventDTO(userIdList, EventType.ITEM_GROUP_UPDATE, groupDTO, group.getLastModifiedBy());
        eventServiceClient.addEvent(dto);
    }

    public void sendGroupDeleteEvent(Group group) {
        List<UUID> userIdList = group.getMembers().stream().map(Member::getUserId).toList();
        GroupDTO groupDTO = groupMapper.pojoToDTO(group);
        EventDTO dto = new EventDTO(userIdList, EventType.ITEM_GROUP_DELETE, groupDTO, group.getLastModifiedBy());
        eventServiceClient.addEvent(dto);
    }

    public void sendItemCreateEvent(Item item) {
        List<UUID> userIdList = item.getGroup().getMembers().stream().map(Member::getUserId).toList();
        ItemDTO itemDTO = itemMapper.pojoToDTO(item);
        EventDTO dto = new EventDTO(userIdList, EventType.ITEM_CREATE, itemDTO, item.getCreatedBy());
        eventServiceClient.addEvent(dto);
    }

    public void sendItemUpdateEvent(Item item) {
        List<UUID> userIdList = item.getGroup().getMembers().stream().map(Member::getUserId).toList();
        ItemDTO itemDTO = itemMapper.pojoToDTO(item);
        EventDTO dto = new EventDTO(userIdList, EventType.ITEM_UPDATE, itemDTO, item.getLastModifiedBy());
        eventServiceClient.addEvent(dto);
    }

    public void sendItemUpdateStatusEvent(Item item) {
        List<UUID> userIdList = item.getGroup().getMembers().stream().map(Member::getUserId).toList();
        ItemDTO itemDTO = itemMapper.pojoToDTO(item);
        EventDTO dto = new EventDTO(userIdList, EventType.ITEM_UPDATE_STATUS, itemDTO, item.getLastModifiedBy());
        eventServiceClient.addEvent(dto);
    }

    public void sendItemUpdateArchivedEvent(Item item) {
        List<UUID> userIdList = item.getGroup().getMembers().stream().map(Member::getUserId).toList();
        ItemDTO itemDTO = itemMapper.pojoToDTO(item);
        EventDTO dto = new EventDTO(userIdList, EventType.ITEM_UPDATE_ARCHIVED, itemDTO, item.getLastModifiedBy());
        eventServiceClient.addEvent(dto);
    }

    public void sendItemDeleteEvent(Item item) {
        List<UUID> userIdList = item.getGroup().getMembers().stream().map(Member::getUserId).toList();
        ItemDTO itemDTO = itemMapper.pojoToDTO(item);
        EventDTO dto = new EventDTO(userIdList, EventType.ITEM_DELETE, itemDTO, item.getLastModifiedBy());
        eventServiceClient.addEvent(dto);
    }

    public void sendMemberAddEvent(Group group, List<Member> memberList, UUID userId) {
        List<UUID> userIdList = group.getMembers().stream().map(Member::getUserId).toList();
        List<MemberDTO> memberDTOList = memberList.stream().map(memberMapper::pojoToDTO).toList();
        EventDTO dto = new EventDTO(userIdList, EventType.ITEM_MEMBER_ADD, memberDTOList, userId);
        eventServiceClient.addEvent(dto);
    }

    public void sendMemberDeleteEvent(Group group, List<Member> memberList, UUID userId) {
        List<UUID> userIdList = group.getMembers().stream().map(Member::getUserId).toList();
        List<MemberDTO> memberDTOList = memberList.stream().map(memberMapper::pojoToDTO).toList();
        EventDTO dto = new EventDTO(userIdList, EventType.ITEM_MEMBER_DELETE, memberDTOList, userId);
        eventServiceClient.addEvent(dto);
    }

    public void sendMemberLeaveEvent(Group group, Member member) {
        List<UUID> userIdList = group.getMembers().stream().map(Member::getUserId).toList();
        MemberDTO memberDTO = memberMapper.pojoToDTO(member);
        EventDTO dto = new EventDTO(userIdList, EventType.ITEM_MEMBER_LEAVE, memberDTO, member.getUserId());
        eventServiceClient.addEvent(dto);
    }

    public void sendMemberRoleEvent(Group group, Member member, UUID userId) {
        List<UUID> userIdList = group.getMembers().stream().map(Member::getUserId).toList();
        MemberDTO memberDTO = memberMapper.pojoToDTO(member);
        EventDTO dto = new EventDTO(userIdList, EventType.ITEM_MEMBER_ROLE, memberDTO, userId);
        eventServiceClient.addEvent(dto);
    }

}
