package com.persoff68.fatodo.client;

import com.persoff68.fatodo.model.Reminder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "notification-service", primary = false)
public interface NotificationServiceClient {

    @PutMapping(value = "/{targetId}")
    void setReminders(@PathVariable UUID targetId, @RequestBody List<Reminder> reminderDTOList);

    @DeleteMapping(value = "/{targetId}")
    void deleteReminders(@PathVariable UUID targetId);

}

