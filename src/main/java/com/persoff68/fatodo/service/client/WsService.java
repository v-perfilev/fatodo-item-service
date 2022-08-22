package com.persoff68.fatodo.service.client;

import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.mapper.GroupMapper;
import com.persoff68.fatodo.mapper.ItemMapper;
import com.persoff68.fatodo.mapper.MemberMapper;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.constant.WsEventType;
import com.persoff68.fatodo.model.dto.GroupDTO;
import com.persoff68.fatodo.model.dto.ItemDTO;
import com.persoff68.fatodo.model.dto.MemberDTO;
import com.persoff68.fatodo.model.dto.WsEventWithUsersDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WsService {

    private final WsServiceClient wsServiceClient;
    private final GroupMapper groupMapper;
    private final ItemMapper itemMapper;
    private final MemberMapper memberMapper;

    public void sendGroupCreateEvent(Group group) {
        List<UUID> userIdList = group.getMembers().stream().map(Member::getUserId).toList();
        GroupDTO groupDTO = groupMapper.pojoToDTO(group);
        WsEventWithUsersDTO dto = new WsEventWithUsersDTO(userIdList, WsEventType.ITEM_GROUP_CREATE, groupDTO);
        wsServiceClient.sendEvent(dto);
    }

    public void sendGroupUpdateEvent(Group group) {
        List<UUID> userIdList = group.getMembers().stream().map(Member::getUserId).toList();
        GroupDTO groupDTO = groupMapper.pojoToDTO(group);
        WsEventWithUsersDTO dto = new WsEventWithUsersDTO(userIdList, WsEventType.ITEM_GROUP_UPDATE, groupDTO);
        wsServiceClient.sendEvent(dto);
    }

    public void sendGroupDeleteEvent(Group group) {
        List<UUID> userIdList = group.getMembers().stream().map(Member::getUserId).toList();
        GroupDTO groupDTO = groupMapper.pojoToDTO(group);
        WsEventWithUsersDTO dto = new WsEventWithUsersDTO(userIdList, WsEventType.ITEM_GROUP_DELETE, groupDTO);
        wsServiceClient.sendEvent(dto);
    }

    public void sendGroupDeleteEvent(Group group, List<UUID> userIdList) {
        GroupDTO groupDTO = groupMapper.pojoToDTO(group);
        WsEventWithUsersDTO dto = new WsEventWithUsersDTO(userIdList, WsEventType.ITEM_GROUP_DELETE, groupDTO);
        wsServiceClient.sendEvent(dto);
    }

    public void sendItemCreateEvent(Item item) {
        List<UUID> userIdList = item.getGroup().getMembers().stream().map(Member::getUserId).toList();
        ItemDTO itemDTO = itemMapper.pojoToDTO(item);
        WsEventWithUsersDTO dto = new WsEventWithUsersDTO(userIdList, WsEventType.ITEM_CREATE, itemDTO);
        wsServiceClient.sendEvent(dto);
    }

    public void sendItemUpdateEvent(Item item) {
        List<UUID> userIdList = item.getGroup().getMembers().stream().map(Member::getUserId).toList();
        ItemDTO itemDTO = itemMapper.pojoToDTO(item);
        WsEventWithUsersDTO dto = new WsEventWithUsersDTO(userIdList, WsEventType.ITEM_UPDATE, itemDTO);
        wsServiceClient.sendEvent(dto);
    }

    public void sendItemDeleteEvent(Item item) {
        List<UUID> userIdList = item.getGroup().getMembers().stream().map(Member::getUserId).toList();
        ItemDTO itemDTO = itemMapper.pojoToDTO(item);
        WsEventWithUsersDTO dto = new WsEventWithUsersDTO(userIdList, WsEventType.ITEM_DELETE, itemDTO);
        wsServiceClient.sendEvent(dto);
    }

    public void sendMemberAddEvent(Group group, List<Member> memberList) {
        List<UUID> userIdList = group.getMembers().stream().map(Member::getUserId).toList();
        List<MemberDTO> memberDTOList = memberList.stream().map(memberMapper::pojoToDTO).toList();
        WsEventWithUsersDTO dto = new WsEventWithUsersDTO(userIdList, WsEventType.ITEM_MEMBER_ADD, memberDTOList);
        wsServiceClient.sendEvent(dto);
    }

    public void sendMemberDeleteEvent(Group group, List<Member> memberList) {
        List<UUID> userIdList = group.getMembers().stream().map(Member::getUserId).toList();
        List<MemberDTO> memberDTOList = memberList.stream().map(memberMapper::pojoToDTO).toList();
        WsEventWithUsersDTO dto = new WsEventWithUsersDTO(userIdList, WsEventType.ITEM_MEMBER_DELETE, memberDTOList);
        wsServiceClient.sendEvent(dto);
    }

    public void sendMemberLeaveEvent(Group group, Member member) {
        List<UUID> userIdList = group.getMembers().stream().map(Member::getUserId).toList();
        MemberDTO memberDTO = memberMapper.pojoToDTO(member);
        WsEventWithUsersDTO dto = new WsEventWithUsersDTO(userIdList, WsEventType.ITEM_MEMBER_LEAVE, memberDTO);
        wsServiceClient.sendEvent(dto);
    }

    public void sendMemberRoleEvent(Group group, Member member) {
        List<UUID> userIdList = group.getMembers().stream().map(Member::getUserId).toList();
        MemberDTO memberDTO = memberMapper.pojoToDTO(member);
        WsEventWithUsersDTO dto = new WsEventWithUsersDTO(userIdList, WsEventType.ITEM_MEMBER_ROLE, memberDTO);
        wsServiceClient.sendEvent(dto);
    }

}
