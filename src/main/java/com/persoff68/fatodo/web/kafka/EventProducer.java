package com.persoff68.fatodo.web.kafka;

import com.persoff68.fatodo.client.EventServiceClient;
import com.persoff68.fatodo.config.annotation.ConditionalOnPropertyNotNull;
import com.persoff68.fatodo.config.constant.KafkaTopics;
import com.persoff68.fatodo.model.dto.CreateItemEventDTO;
import com.persoff68.fatodo.model.dto.DeleteEventsDTO;
import com.persoff68.fatodo.model.dto.DeleteUserEventsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnPropertyNotNull(value = "kafka.bootstrapAddress")
public class EventProducer implements EventServiceClient {

    private final KafkaTemplate<String, CreateItemEventDTO> eventItemKafkaTemplate;
    private final KafkaTemplate<String, DeleteUserEventsDTO> eventDeleteUserKafkaTemplate;
    private final KafkaTemplate<String, DeleteEventsDTO> eventDeleteItemKafkaTemplate;

    @Override
    public void addItemEvent(CreateItemEventDTO createItemEventDTO) {
        eventItemKafkaTemplate.send(KafkaTopics.EVENT_ADD.getValue(), "item", createItemEventDTO);
    }

    @Override
    public void deleteGroupEventsForUsers(DeleteUserEventsDTO deleteUserEventsDTO) {
        eventDeleteUserKafkaTemplate.send(KafkaTopics.EVENT_DELETE.getValue(), "group-delete-users",
                deleteUserEventsDTO);
    }

    @Override
    public void deleteGroupEvents(DeleteEventsDTO deleteEventsDTO) {
        eventDeleteItemKafkaTemplate.send(KafkaTopics.EVENT_DELETE.getValue(), "group-delete",
                deleteEventsDTO);
    }

    @Override
    public void deleteItemEvents(DeleteEventsDTO deleteEventsDTO) {
        eventDeleteItemKafkaTemplate.send(KafkaTopics.EVENT_DELETE.getValue(), "item-delete",
                deleteEventsDTO);
    }

}
