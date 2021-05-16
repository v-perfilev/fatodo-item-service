package com.persoff68.fatodo.service.helper;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.security.exception.UnauthorizedException;
import com.persoff68.fatodo.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PermissionHelper {

    public boolean canRead(Group group) {
        UUID userId = SecurityUtils.getCurrentId()
                .orElseThrow(UnauthorizedException::new);
        return hasReadPermission(group, userId);
    }

    public boolean canRead(List<Group> groupList) {
        UUID userId = SecurityUtils.getCurrentId()
                .orElseThrow(UnauthorizedException::new);
        return groupList.stream().allMatch(group -> hasReadPermission(group, userId));
    }

    public boolean canEdit(Group group) {
        UUID userId = SecurityUtils.getCurrentId()
                .orElseThrow(UnauthorizedException::new);
        return hasEditPermission(group, userId);
    }

    public boolean canEdit(List<Group> groupList) {
        UUID userId = SecurityUtils.getCurrentId()
                .orElseThrow(UnauthorizedException::new);
        return groupList.stream().allMatch(group -> hasEditPermission(group, userId));
    }

    public boolean canAdmin(Group group) {
        UUID userId = SecurityUtils.getCurrentId()
                .orElseThrow(UnauthorizedException::new);
        return hasAdminPermission(group, userId);
    }

    public boolean canAdmin(List<Group> groupList) {
        UUID userId = SecurityUtils.getCurrentId()
                .orElseThrow(UnauthorizedException::new);
        return groupList.stream().allMatch(group -> hasAdminPermission(group, userId));
    }

    private boolean hasReadPermission(Group group, UUID userId) {
        return group.getUsers().stream()
                .anyMatch(user -> user.getId().equals(userId));
    }

    private boolean hasEditPermission(Group group, UUID userId) {
        return group.getUsers().stream()
                .filter(user -> user.getId().equals(userId))
                .anyMatch(user -> user.getPermission().equals(Permission.ADMIN)
                        || user.getPermission().equals(Permission.EDIT));
    }

    private boolean hasAdminPermission(Group group, UUID userId) {
        return group.getUsers().stream()
                .filter(user -> user.getId().equals(userId))
                .anyMatch(user -> user.getPermission().equals(Permission.ADMIN));
    }

}
