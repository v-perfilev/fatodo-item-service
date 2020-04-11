package com.persoff68.fatodo.service.util;

import com.persoff68.fatodo.client.GroupServiceClient;
import com.persoff68.fatodo.model.dto.GroupPermissionDTO;
import com.persoff68.fatodo.security.jwt.JwtTokenProvider;
import com.persoff68.fatodo.security.util.SecurityUtils;
import com.persoff68.fatodo.service.exception.PermissionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PermissionHelper {

    private final JwtTokenProvider jwtTokenProvider;
    private final GroupServiceClient groupServiceClient;

    public boolean canRead(List<String> groupIds) {
        GroupPermissionDTO dto = new GroupPermissionDTO(groupIds, getUserId());
        return groupServiceClient.canRead(dto);
    }

    public boolean canEdit(List<String> groupIds) {
        GroupPermissionDTO dto = new GroupPermissionDTO(groupIds, getUserId());
        return groupServiceClient.canEdit(dto);
    }

    public boolean canAdmin(List<String> groupIds) {
        GroupPermissionDTO dto = new GroupPermissionDTO(groupIds, getUserId());
        return groupServiceClient.canAdmin(dto);
    }

    private String getUserId() {
        String jwt = SecurityUtils.getCurrentJwt()
                .orElseThrow(PermissionException::new);
        return jwtTokenProvider.getUserIdFromJwt(jwt);
    }

}
