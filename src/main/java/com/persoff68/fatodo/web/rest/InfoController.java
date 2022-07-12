package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.model.ReminderMailInfo;
import com.persoff68.fatodo.model.dto.ReminderMailInfoDTO;
import com.persoff68.fatodo.model.mapper.ReminderMapper;
import com.persoff68.fatodo.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(InfoController.ENDPOINT)
@RequiredArgsConstructor
public class InfoController {
    static final String ENDPOINT = "/api/info";

    private final ReminderService reminderService;
    private final ReminderMapper reminderMapper;

    @GetMapping(value = "/item-reminder/{itemId}")
    public ResponseEntity<ReminderMailInfoDTO> getReminderForItem(@PathVariable UUID itemId) {
        ReminderMailInfo message = reminderService.generateByItemId(itemId);
        ReminderMailInfoDTO dto = reminderMapper.pojoToDTO(message);
        return ResponseEntity.ok(dto);
    }

}
