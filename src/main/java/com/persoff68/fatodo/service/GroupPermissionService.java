package com.persoff68.fatodo.service;

import com.persoff68.fatodo.client.GroupServiceClient;
import com.persoff68.fatodo.model.dto.GroupPermissionDTO;
import com.persoff68.fatodo.security.jwt.JwtTokenProvider;
import com.persoff68.fatodo.security.util.SecurityUtils;
import com.persoff68.fatodo.service.exception.PermissionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GroupPermissionService {

    private final JwtTokenProvider jwtTokenProvider;
    private final GroupServiceClient groupServiceClient;

    public void checkReadPermission(Set<String> groupIds) {
        if (!canRead(groupIds)) {
            throw new PermissionException();
        }
    }

    public void checkReadPermission(String groupId) {
        checkReadPermission(Collections.singleton(groupId));
    }

    public void checkEditPermission(Set<String> groupIds) {
        if (!canEdit(groupIds)) {
            throw new PermissionException();
        }
    }

    public void checkEditPermission(String groupId) {
        checkEditPermission(Collections.singleton(groupId));
    }

    public void checkAdminPermission(Set<String> groupIds) {
        if (!canAdmin(groupIds)) {
            throw new PermissionException();
        }
    }

    public void checkAdminPermission(String groupId) {
        checkAdminPermission(Collections.singleton(groupId));
    }

    private boolean canRead(Set<String> groupIds) {
        GroupPermissionDTO dto = new GroupPermissionDTO(groupIds, getUserId());
        return groupServiceClient.canRead(dto);
    }

    private boolean canEdit(Set<String> groupIds) {
        GroupPermissionDTO dto = new GroupPermissionDTO(groupIds, getUserId());
        return groupServiceClient.canEdit(dto);
    }

    private boolean canAdmin(Set<String> groupIds) {
        GroupPermissionDTO dto = new GroupPermissionDTO(groupIds, getUserId());
        return groupServiceClient.canEdit(dto);
    }

    private String getUserId() {
        String jwt = SecurityUtils.getCurrentJwt()
                .orElseThrow(PermissionException::new);
        return jwtTokenProvider.getUserIdFromJwt(jwt);
    }

}
