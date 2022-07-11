package com.persoff68.fatodo.contract;

import com.persoff68.fatodo.builder.TestReminder;
import com.persoff68.fatodo.client.NotificationServiceClient;
import com.persoff68.fatodo.model.Reminder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@AutoConfigureStubRunner(ids = {"com.persoff68.fatodo:notificationservice:+:stubs"},
        stubsMode = StubRunnerProperties.StubsMode.REMOTE)
class NotificationServiceCT {

    @Autowired
    NotificationServiceClient notificationServiceClient;

    @Test
    void testSetReminders() {
        UUID targetId = UUID.randomUUID();
        Reminder reminder = TestReminder.defaultBuilder().build().toParent();
        List<Reminder> reminderList = Collections.singletonList(reminder);
        assertDoesNotThrow(() -> notificationServiceClient.setReminders(targetId, reminderList));
    }

    @Test
    void testDeleteRemindersByParentId() {
        UUID id = UUID.randomUUID();
        assertDoesNotThrow(() -> notificationServiceClient.deleteRemindersByParentId(id));
    }

    @Test
    void testDeleteRemindersByTargetId() {
        UUID id = UUID.randomUUID();
        assertDoesNotThrow(() -> notificationServiceClient.deleteRemindersByTargetId(id));
    }

}
