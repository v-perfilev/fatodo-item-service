package com.persoff68.fatodo.client;

import com.persoff68.fatodo.exception.ClientException;
import com.persoff68.fatodo.model.dto.CreateItemEventDTO;
import com.persoff68.fatodo.model.dto.DeleteEventsDTO;
import com.persoff68.fatodo.model.dto.DeleteUserEventsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventServiceClientWrapper implements EventServiceClient {

    @Qualifier("feignEventServiceClient")
    private final EventServiceClient eventServiceClient;

    @Override
    public void addItemEvent(CreateItemEventDTO createItemEventDTO) {
        try {
            eventServiceClient.addItemEvent(createItemEventDTO);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public void deleteGroupEventsForUsers(DeleteUserEventsDTO deleteUserEventsDTO) {
        try {
            eventServiceClient.deleteGroupEventsForUsers(deleteUserEventsDTO);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public void deleteGroupEvents(DeleteEventsDTO deleteEventsDTO) {
        try {
            eventServiceClient.deleteGroupEvents(deleteEventsDTO);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

    @Override
    public void deleteItemEvents(DeleteEventsDTO deleteEventsDTO) {
        try {
            eventServiceClient.deleteItemEvents(deleteEventsDTO);
        } catch (Exception e) {
            throw new ClientException();
        }
    }

}
