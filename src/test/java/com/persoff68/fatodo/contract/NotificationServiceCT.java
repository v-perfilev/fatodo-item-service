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
        UUID id = UUID.randomUUID();
        Reminder reminder = TestReminder.defaultBuilder().build();
        assertDoesNotThrow(() -> notificationServiceClient.setReminders(id, Collections.singletonList(reminder)));
    }

    @Test
    void testDeleteReminders() {
        UUID id = UUID.randomUUID();
        assertDoesNotThrow(() -> notificationServiceClient.deleteReminders(id));
    }

}
