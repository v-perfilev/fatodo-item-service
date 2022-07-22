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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(PermissionController.ENDPOINT)
@RequiredArgsConstructor
public class PermissionController {

    static final String ENDPOINT = "/api/permission";

    private final PermissionService permissionService;
    private final GroupService groupService;

    @GetMapping(value = "/group")
    public ResponseEntity<List<UUID>> getGroupIdsForMember() {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        List<UUID> groupIdList = groupService.getAllIdsByUserId(userId);
        return ResponseEntity.ok(groupIdList);
    }

    @GetMapping(value = "/group/{permission}/check")
    public ResponseEntity<Boolean> hasGroupsPermission(@PathVariable Permission permission,
                                                       @RequestParam("ids") List<UUID> groupIdList) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        boolean hasPermission = permissionService.hasGroupsPermission(userId, permission, groupIdList);
        return ResponseEntity.ok(hasPermission);
    }

    @GetMapping(value = "/group/{permission}/ids")
    public ResponseEntity<List<UUID>> getAllowedGroupsIds(@PathVariable Permission permission,
                                                          @RequestParam("ids") List<UUID> groupIdList) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        List<UUID> idList = permissionService.getAllowedGroupsIds(userId, permission, groupIdList);
        return ResponseEntity.ok(idList);
    }

    @GetMapping(value = "/item/{permission}/check")
    public ResponseEntity<Boolean> hasItemsPermission(@PathVariable Permission permission,
                                                      @RequestParam("ids") List<UUID> itemIdList) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        boolean hasPermission = permissionService.hasItemsPermission(userId, permission, itemIdList);
        return ResponseEntity.ok(hasPermission);
    }

    @GetMapping(value = "/item/{permission}/ids")
    public ResponseEntity<List<UUID>> getAllowedItemIds(@PathVariable Permission permission,
                                                        @RequestParam("ids") List<UUID> itemIdList) {
        UUID userId = SecurityUtils.getCurrentId().orElseThrow(UnauthorizedException::new);
        List<UUID> idList = permissionService.getAllowedItemIds(userId, permission, itemIdList);
        return ResponseEntity.ok(idList);
    }

}
