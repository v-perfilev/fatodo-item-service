package com.persoff68.fatodo.service;

import com.persoff68.fatodo.client.GroupServiceClient;
import com.persoff68.fatodo.model.dto.GroupPermissionDTO;
import com.persoff68.fatodo.security.jwt.JwtTokenProvider;
import com.persoff68.fatodo.security.util.SecurityUtils;
import com.persoff68.fatodo.service.exception.PermissionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final JwtTokenProvider jwtTokenProvider;
    private final GroupServiceClient groupServiceClient;

    public void checkRead(String groupId) {
        checkRead(Set.of(groupId));
    }

    public void checkRead(Set<String> groupIds) {
        if (!canRead(groupIds)) {
            throw new PermissionException();
        }
    }

    public void checkEdit(String groupId) {
        checkEdit(Set.of(groupId));
    }

    public void checkEdit(Set<String> groupIds) {
        if (!canEdit(groupIds)) {
            throw new PermissionException();
        }
    }

    public void checkAdmin(String groupId) {
        checkAdmin(Set.of(groupId));
    }

    public void checkAdmin(Set<String> groupIds) {
        if (!canAdmin(groupIds)) {
            throw new PermissionException();
        }
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
        return groupServiceClient.canAdmin(dto);
    }


    private String getUserId() {
        String jwt = SecurityUtils.getCurrentJwt()
                .orElseThrow(PermissionException::new);
        return jwtTokenProvider.getUserIdFromJwt(jwt);
    }

}
