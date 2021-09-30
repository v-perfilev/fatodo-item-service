package com.persoff68.fatodo.service.helper;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.security.exception.UnauthorizedException;
import com.persoff68.fatodo.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PermissionHelper {

    public boolean canRead(Group group) {
        UUID userId = SecurityUtils.getCurrentId()
                .orElseThrow(UnauthorizedException::new);
        return group.getMembers().stream()
                .anyMatch(user -> user.getId().equals(userId));
    }

    public boolean canEdit(Group group) {
        UUID userId = SecurityUtils.getCurrentId()
                .orElseThrow(UnauthorizedException::new);
        return group.getMembers().stream()
                .filter(user -> user.getId().equals(userId))
                .anyMatch(user -> user.getPermission().equals(Permission.ADMIN)
                        || user.getPermission().equals(Permission.EDIT));
    }

    public boolean canAdmin(Group group) {
        UUID userId = SecurityUtils.getCurrentId()
                .orElseThrow(UnauthorizedException::new);
        return group.getMembers().stream()
                .filter(user -> user.getId().equals(userId))
                .anyMatch(user -> user.getPermission().equals(Permission.ADMIN));
    }

}
