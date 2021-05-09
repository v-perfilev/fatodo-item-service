package com.persoff68.fatodo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "group-service", primary = false)
public interface GroupServiceClient {

    @PostMapping(value = "/api/permissions/read",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    boolean canRead(List<UUID> groupIds);

    @PostMapping(value = "/api/permissions/edit",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    boolean canEdit(List<UUID> groupIds);

    @PostMapping(value = "/api/permissions/admin",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    boolean canAdmin(List<UUID> groupIds);

}
