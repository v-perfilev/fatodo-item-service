package com.persoff68.fatodo.client;

import com.persoff68.fatodo.exception.ClientException;
import com.persoff68.fatodo.model.Reminder;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationServiceClientWrapper implements NotificationServiceClient {

    @Qualifier("feignNotificationServiceClient")
    private final NotificationServiceClient notificationServiceClient;

    @Override
    public void setReminders(UUID targetId, List<Reminder> reminderDTOList) {
        try {
            notificationServiceClient.setReminders(targetId, reminderDTOList);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public void deleteReminders(UUID targetId) {
        try {
            notificationServiceClient.deleteReminders(targetId);
        } catch (FeignException.NotFound e) {
            // skip
        } catch (Exception e) {
            throw new ClientException();
        }
    }
}
