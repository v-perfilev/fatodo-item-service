package com.persoff68.fatodo.service.client;

import com.persoff68.fatodo.client.WsServiceClient;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.constant.ClearEventType;
import com.persoff68.fatodo.model.dto.ClearEventDTO;
import com.persoff68.fatodo.model.dto.WsEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WsService {

    private final WsServiceClient wsServiceClient;

    public void sendClearGroupEvent(Group group, List<UUID> userIdList) {
        sendClearEvent(ClearEventType.GROUP, group.getId(), userIdList);
    }

    public void sendClearGroupEvent(Group group) {
        List<UUID> userIdList = group.getMembers().stream()
                .map(Member::getUserId)
                .toList();
        sendClearEvent(ClearEventType.GROUP, group.getId(), userIdList);
    }

    public void sendClearItemEvent(Item item) {
        Group group = item.getGroup();
        List<UUID> userIdList = group.getMembers().stream()
                .map(Member::getUserId)
                .toList();
        sendClearEvent(ClearEventType.ITEM, item.getId(), userIdList);
    }

    public void sendClearEvent(ClearEventType type, UUID id, List<UUID> userIdList) {
        ClearEventDTO clearEventDTO = new ClearEventDTO();
        clearEventDTO.setType(type);
        clearEventDTO.setId(id);
        WsEventDTO<ClearEventDTO> eventDTO = new WsEventDTO<>(userIdList, clearEventDTO);
        sendClearEventAsync(eventDTO);
    }

    @Async
    public void sendClearEventAsync(WsEventDTO<ClearEventDTO> event) {
        wsServiceClient.sendClearEvent(event);
    }

}
