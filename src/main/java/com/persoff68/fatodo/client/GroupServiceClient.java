package com.persoff68.fatodo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "group-service", fallbackFactory = GroupServiceFallbackFactory.class)
public interface GroupServiceClient {

    @PostMapping(value = "/api/permission/read",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    boolean canRead(List<String> groupIds);

    @PostMapping(value = "/api/permission/edit",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    boolean canEdit(List<String> groupIds);

    @PostMapping(value = "/api/permission/admin",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    boolean canAdmin(List<String> groupIds);

    @GetMapping(value = "/api/group/ids",
            produces = MediaType.APPLICATION_JSON_VALUE)
    List<String> getAllGroupIdsForUser();

}
