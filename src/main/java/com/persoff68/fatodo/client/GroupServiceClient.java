package com.persoff68.fatodo.client;

import com.persoff68.fatodo.model.dto.GroupPermissionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("group-service")
public interface GroupServiceClient {

    @PostMapping(value = "/permission/read",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    boolean canRead(GroupPermissionDTO groupPermissionDTO);

    @PostMapping(value = "/permission/edit",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    boolean canEdit(GroupPermissionDTO groupPermissionDTO);

    @PostMapping(value = "/permission/admin",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    boolean canAdmin(GroupPermissionDTO groupPermissionDTO);

}
