package com.persoff68.fatodo.client;

import com.persoff68.fatodo.client.configuration.FeignSystemConfiguration;
import com.persoff68.fatodo.model.dto.CreateItemEventDTO;
import com.persoff68.fatodo.model.dto.DeleteEventsDTO;
import com.persoff68.fatodo.model.dto.DeleteUserEventsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "event-service", primary = false,
        configuration = {FeignSystemConfiguration.class},
        qualifiers = {"feignEventServiceClient"})
public interface EventServiceClient {

    @PostMapping(value = "/api/event/item")
    void addItemEvent(@RequestBody CreateItemEventDTO createItemEventDTO);

    @PostMapping(value = "/api/event/group/delete-users")
    void deleteGroupEventsForUsers(@RequestBody DeleteUserEventsDTO deleteUserEventsDTO);

    @PostMapping("/api/event/group/delete")
    void deleteGroupEvents(@RequestBody DeleteEventsDTO deleteEventsDTO);

    @PostMapping("/api/event/item/delete")
    void deleteItemEvents(@RequestBody DeleteEventsDTO deleteEventsDTO);

}
