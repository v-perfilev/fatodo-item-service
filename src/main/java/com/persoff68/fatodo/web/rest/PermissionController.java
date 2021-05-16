package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(PermissionController.ENDPOINT)
@RequiredArgsConstructor
public class PermissionController {

    static final String ENDPOINT = "/api/permissions";

    private final PermissionService permissionService;

    @GetMapping(value = "/group/{groupId}")
    public ResponseEntity<Boolean> canReadGroup(@PathVariable UUID groupId) {
        boolean hasPermission = permissionService.hasReadPermission(groupId);
        return ResponseEntity.ok(hasPermission);
    }

    @GetMapping(value = "/item/{itemId}")
    public ResponseEntity<Boolean> canReadItem(@PathVariable UUID itemId) {
        boolean hasPermission = permissionService.hasReadItemPermission(itemId);
        return ResponseEntity.ok(hasPermission);
    }

}
