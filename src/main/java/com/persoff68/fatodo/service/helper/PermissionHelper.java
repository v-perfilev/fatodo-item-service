package com.persoff68.fatodo.service.helper;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.constant.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PermissionHelper {

    public boolean canRead(UUID userId, Group group) {
        return group.getMembers().stream()
                .anyMatch(member -> member.getUserId().equals(userId));
    }

    public boolean canEdit(UUID userId, Group group) {
        return group.getMembers().stream()
                .filter(member -> member.getUserId().equals(userId))
                .anyMatch(member -> member.getPermission().equals(Permission.ADMIN)
                        || member.getPermission().equals(Permission.EDIT));
    }

    public boolean canAdmin(UUID userId, Group group) {
        return group.getMembers().stream()
                .filter(member -> member.getUserId().equals(userId))
                .anyMatch(member -> member.getPermission().equals(Permission.ADMIN));
    }

}
