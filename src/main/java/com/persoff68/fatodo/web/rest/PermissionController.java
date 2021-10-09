package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(PermissionController.ENDPOINT)
@RequiredArgsConstructor
public class PermissionController {

    static final String ENDPOINT = "/api/permissions";

    private final PermissionService permissionService;

    @GetMapping(value = "/group/read/{groupId}")
    public ResponseEntity<Boolean> canReadGroup(@PathVariable UUID groupId) {
        boolean hasPermission = permissionService.hasReadPermission(groupId);
        return ResponseEntity.ok(hasPermission);
    }

    @PostMapping(value = "/groups/admin")
    public ResponseEntity<Boolean> canAdminGroups(@RequestBody List<UUID> groupIdList) {
        boolean hasPermission = permissionService.hasMultipleAdminPermission(groupIdList);
        return ResponseEntity.ok(hasPermission);
    }

    @GetMapping(value = "/item/read/{itemId}")
    public ResponseEntity<Boolean> canReadItem(@PathVariable UUID itemId) {
        boolean hasPermission = permissionService.hasReadItemPermission(itemId);
        return ResponseEntity.ok(hasPermission);
    }

    @PostMapping(value = "/items/admin")
    public ResponseEntity<Boolean> canAdminItems(@RequestBody List<UUID> itemIdList) {
        boolean hasPermission = permissionService.hasMultipleAdminItemPermission(itemIdList);
        return ResponseEntity.ok(hasPermission);
    }

}
