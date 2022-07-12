package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.model.ReminderMessage;
import com.persoff68.fatodo.model.dto.ReminderMessageDTO;
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
    static final String ENDPOINT = "/api/reminders";

    private final ReminderService reminderService;
    private final ReminderMapper reminderMapper;

    @GetMapping(value = "/item/{itemId}")
    public ResponseEntity<ReminderMessageDTO> getReminderForItem(@PathVariable UUID itemId) {
        ReminderMessage message = reminderService.generateByItemId(itemId);
        ReminderMessageDTO dto = reminderMapper.pojoToDTO(message);
        return ResponseEntity.ok(dto);
    }

}
