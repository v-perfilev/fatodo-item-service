package com.persoff68.fatodo.client;

import com.persoff68.fatodo.exception.ClientException;
import com.persoff68.fatodo.model.Reminder;
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
    public void setReminders(UUID parentId, UUID targetId, List<Reminder> reminderDTOList) {
        try {
            notificationServiceClient.setReminders(parentId, targetId, reminderDTOList);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public void deleteRemindersByParentId(UUID parentId) {
        try {
            notificationServiceClient.deleteRemindersByParentId(parentId);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public void deleteRemindersByTargetId(UUID targetId) {
        try {
            notificationServiceClient.deleteRemindersByTargetId(targetId);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

}
