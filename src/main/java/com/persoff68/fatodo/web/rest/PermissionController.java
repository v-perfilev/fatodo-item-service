package com.persoff68.fatodo.web.rest;

import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.security.exception.UnauthorizedException;
import com.persoff68.fatodo.security.util.SecurityUtils;
import com.persoff68.fatodo.service.GroupService;
import com.persoff68.fatodo.service.client.PermissionService;
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
    private final GroupService groupService;

    @GetMapping(value = "/groups")
    public ResponseEntity<List<UUID>> getGroupIdsForMember() {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        List<UUID> groupIdList = groupService.getAllIdsByUserId(userId);
        return ResponseEntity.ok(groupIdList);
    }

    @PostMapping(value = "/groups/{permission}")
    public ResponseEntity<Boolean> hasGroupsPermission(@PathVariable Permission permission,
                                                       @RequestBody List<UUID> groupIdList) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        boolean hasPermission = permissionService.hasGroupsPermission(userId, permission, groupIdList);
        return ResponseEntity.ok(hasPermission);
    }

    @PostMapping(value = "/groups/{permission}/ids")
    public ResponseEntity<List<UUID>> getAllowedGroupsIds(@PathVariable Permission permission,
                                                          @RequestBody List<UUID> groupIdList) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        List<UUID> idList = permissionService.getAllowedGroupsIds(userId, permission, groupIdList);
        return ResponseEntity.ok(idList);
    }

    @PostMapping(value = "/items/{permission}")
    public ResponseEntity<Boolean> hasItemsPermission(@PathVariable Permission permission,
                                                      @RequestBody List<UUID> itemIdList) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        boolean hasPermission = permissionService.hasItemsPermission(userId, permission, itemIdList);
        return ResponseEntity.ok(hasPermission);
    }

    @PostMapping(value = "/items/{permission}/ids")
    public ResponseEntity<List<UUID>> getAllowedItemIds(@PathVariable Permission permission,
                                                        @RequestBody List<UUID> itemIdList) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        List<UUID> idList = permissionService.getAllowedItemIds(userId, permission, itemIdList);
        return ResponseEntity.ok(idList);
    }

}
