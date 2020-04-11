package com.persoff68.fatodo.client;

import com.persoff68.fatodo.model.dto.GroupPermissionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Set;

@FeignClient("group-service")
public interface GroupServiceClient {

    @PostMapping(value = "/check/read",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    boolean canRead(GroupPermissionDTO groupPermissionDTO);

    @PostMapping(value = "/check/write",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    boolean canEdit(GroupPermissionDTO groupPermissionDTO);

    @GetMapping(value = "/group/ids-by-user-id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    Set<String> getGroupIdsByUserId(@PathVariable String id);

}
