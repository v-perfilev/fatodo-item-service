package com.persoff68.fatodo.client;

import com.persoff68.fatodo.client.configuration.FeignSystemConfiguration;
import com.persoff68.fatodo.model.dto.ClearEventDTO;
import com.persoff68.fatodo.model.dto.WsEventDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ws-service", primary = false,
        configuration = {FeignSystemConfiguration.class},
        qualifiers = {"feignWsServiceClient"})
public interface WsServiceClient {

    @PostMapping(value = "/api/clear")
    void sendClearEvent(@RequestBody WsEventDTO<ClearEventDTO> event);

}

