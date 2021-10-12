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

    @GetMapping(value = "/group/edit/{groupId}")
    public ResponseEntity<Boolean> canEditGroup(@PathVariable UUID groupId) {
        boolean hasPermission = permissionService.hasEditPermission(groupId);
        return ResponseEntity.ok(hasPermission);
    }

    @GetMapping(value = "/group/admin/{groupId}")
    public ResponseEntity<Boolean> canAdminGroup(@PathVariable UUID groupId) {
        boolean hasPermission = permissionService.hasAdminPermission(groupId);
        return ResponseEntity.ok(hasPermission);
    }

    @GetMapping(value = "/item/read/{itemId}")
    public ResponseEntity<Boolean> canReadItem(@PathVariable UUID itemId) {
        boolean hasPermission = permissionService.hasReadItemPermission(itemId);
        return ResponseEntity.ok(hasPermission);
    }

    @GetMapping(value = "/item/edit/{itemId}")
    public ResponseEntity<Boolean> canEditItem(@PathVariable UUID itemId) {
        boolean hasPermission = permissionService.hasEditItemPermission(itemId);
        return ResponseEntity.ok(hasPermission);
    }

    @GetMapping(value = "/item/admin/{itemId}")
    public ResponseEntity<Boolean> canAdminItem(@PathVariable UUID itemId) {
        boolean hasPermission = permissionService.hasAdminItemPermission(itemId);
        return ResponseEntity.ok(hasPermission);
    }

    @PostMapping(value = "/groups/admin")
    public ResponseEntity<Boolean> canAdminGroups(@RequestBody List<UUID> groupIdList) {
        boolean hasPermission = permissionService.hasMultipleAdminPermission(groupIdList);
        return ResponseEntity.ok(hasPermission);
    }

    @PostMapping(value = "/items/admin")
    public ResponseEntity<Boolean> canAdminItems(@RequestBody List<UUID> itemIdList) {
        boolean hasPermission = permissionService.hasMultipleAdminItemPermission(itemIdList);
        return ResponseEntity.ok(hasPermission);
    }

}
