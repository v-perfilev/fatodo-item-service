package com.persoff68.fatodo.contract;

import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.model.dto.CreateItemEventDTO;
import com.persoff68.fatodo.model.dto.DeleteEventsDTO;
import com.persoff68.fatodo.model.dto.DeleteUserEventsDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@AutoConfigureStubRunner(ids = {"com.persoff68.fatodo:eventservice:+:stubs"},
        stubsMode = StubRunnerProperties.StubsMode.REMOTE)
class EventServiceCT {

    @Autowired
    EventServiceClient eventServiceClient;

    @Test
    void testAddItemEvent() {
        UUID userId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();
        List<UUID> recipientIdList = List.of(userId);
        CreateItemEventDTO dto = CreateItemEventDTO.groupCreate(recipientIdList, userId, groupId);
        assertDoesNotThrow(() -> eventServiceClient.addItemEvent(dto));
    }

    @Test
    void testDeleteGroupEvents() {
        UUID groupId = UUID.randomUUID();
        DeleteEventsDTO dto = new DeleteEventsDTO(groupId);
        assertDoesNotThrow(() -> eventServiceClient.deleteGroupEvents(dto));
    }

    @Test
    void testDeleteItemEvents() {
        UUID itemId = UUID.randomUUID();
        DeleteEventsDTO dto = new DeleteEventsDTO(itemId);
        assertDoesNotThrow(() -> eventServiceClient.deleteItemEvents(dto));
    }

    @Test
    void testDeleteGroupEventsForUsers() {
        UUID groupId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        DeleteUserEventsDTO dto = new DeleteUserEventsDTO(groupId, List.of(userId));
        assertDoesNotThrow(() -> eventServiceClient.deleteGroupEventsForUsers(dto));
    }

}
