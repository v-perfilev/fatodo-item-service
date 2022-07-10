package com.persoff68.fatodo.service.helper;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Member;
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
        return group.getMembers().stream()
                .anyMatch(member -> member.getUserId().equals(userId));
    }

    public boolean canEdit(Group group) {
        UUID userId = SecurityUtils.getCurrentId()
                .orElseThrow(UnauthorizedException::new);
        return group.getMembers().stream()
                .filter(member -> member.getUserId().equals(userId))
                .anyMatch(member -> member.getPermission().equals(Permission.ADMIN)
                        || member.getPermission().equals(Permission.EDIT));
    }

    public boolean canAdmin(Group group) {
        UUID userId = SecurityUtils.getCurrentId()
                .orElseThrow(UnauthorizedException::new);
        return group.getMembers().stream()
                .filter(member -> member.getUserId().equals(userId))
                .anyMatch(member -> member.getPermission().equals(Permission.ADMIN));
    }

}
