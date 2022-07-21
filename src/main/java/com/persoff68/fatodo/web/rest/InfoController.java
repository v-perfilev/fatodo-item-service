package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.ReminderMailInfo;
import com.persoff68.fatodo.model.dto.GroupInfoDTO;
import com.persoff68.fatodo.model.dto.ItemInfoDTO;
import com.persoff68.fatodo.model.dto.ReminderMailInfoDTO;
import com.persoff68.fatodo.mapper.GroupMapper;
import com.persoff68.fatodo.mapper.ItemMapper;
import com.persoff68.fatodo.mapper.ReminderMapper;
import com.persoff68.fatodo.security.exception.UnauthorizedException;
import com.persoff68.fatodo.security.util.SecurityUtils;
import com.persoff68.fatodo.service.GroupService;
import com.persoff68.fatodo.service.ItemService;
import com.persoff68.fatodo.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(InfoController.ENDPOINT)
@RequiredArgsConstructor
public class InfoController {
    static final String ENDPOINT = "/api/info";

    private final InfoService infoService;
    private final GroupService groupService;
    private final ItemService itemService;
    private final ReminderMapper reminderMapper;
    private final GroupMapper groupMapper;
    private final ItemMapper itemMapper;

    @PostMapping(value = "/groups", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GroupInfoDTO>> getAllGroupInfoByIds(@RequestBody List<UUID> groupIdList) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        List<Group> groupList = groupService.getAllByIds(userId, groupIdList);
        List<GroupInfoDTO> dtoList = groupList.stream()
                .map(groupMapper::pojoToInfoDTO)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @PostMapping(value = "/items", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ItemInfoDTO>> getAllItemInfoByIds(@RequestBody List<UUID> itemIdList) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        List<Item> itemList = itemService.getAllByIds(userId, itemIdList);
        List<ItemInfoDTO> dtoList = itemList.stream()
                .map(itemMapper::pojoToInfoDTO)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping(value = "/item-reminder/{itemId}")
    public ResponseEntity<ReminderMailInfoDTO> getReminderForItem(@PathVariable UUID itemId) {
        ReminderMailInfo message = infoService.generateReminderMailInfo(itemId);
        ReminderMailInfoDTO dto = reminderMapper.pojoToDTO(message);
        return ResponseEntity.ok(dto);
    }

}
