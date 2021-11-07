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

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureStubRunner(ids = {"com.persoff68.fatodo:notificationservice:+:stubs"},
        stubsMode = StubRunnerProperties.StubsMode.REMOTE)
public class NotificationServiceCT {

    @Autowired
    NotificationServiceClient notificationServiceClient;

    @Test
    void testSetReminders() {
        UUID id = UUID.randomUUID();
        Reminder reminder = TestReminder.defaultBuilder().build();
        notificationServiceClient.setReminders(id, Collections.singletonList(reminder));
        assertThat(true).isTrue();
    }

    @Test
    void testDeleteReminders() {
        UUID id = UUID.randomUUID();
        notificationServiceClient.deleteReminders(id);
        assertThat(true).isTrue();
    }

}
