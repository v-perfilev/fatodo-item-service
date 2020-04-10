package com.persoff68.fatodo.service;

import com.persoff68.fatodo.client.interceptor.GroupServiceClient;
import com.persoff68.fatodo.config.constant.AuthorityType;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.dto.GroupPermissionDTO;
import com.persoff68.fatodo.security.jwt.JwtTokenProvider;
import com.persoff68.fatodo.security.util.SecurityUtils;
import com.persoff68.fatodo.service.exception.PermissionException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PermissionCheckService {

    private final JwtTokenProvider jwtTokenProvider;
    private final GroupServiceClient groupServiceClient;

    public void checkReadPermission(Item item) {
        if (!canReadItem(item)) {
            throw new PermissionException();
        }
    }

    public void checkReadPermission(String groupId) {
        if (!canReadGroup(groupId)) {
            throw new PermissionException();
        }
    }

    public void checkWritePermission(Item item) {
        if (!canWriteItem(item)) {
            throw new PermissionException();
        }
    }

    private boolean canReadItem(Item item) {
        if (isAdmin()) {
            return true;
        }
        Set<String> groupIds = item.getGroupIds();
        String userId = getUserId();
        GroupPermissionDTO dto = new GroupPermissionDTO(groupIds, userId);
        return groupServiceClient.canRead(dto);
    }

    private boolean canReadGroup(String groupId) {
        if (isAdmin()) {
            return true;
        }
        Set<String> groupIds = Collections.singleton(groupId);
        String userId = getUserId();
        GroupPermissionDTO dto = new GroupPermissionDTO(groupIds, userId);
        return groupServiceClient.canRead(dto);
    }

    private boolean canWriteItem(Item item) {
        if (isAdmin()) {
            return true;
        }
        Set<String> groupIds = item.getGroupIds();
        String userId = getUserId();
        GroupPermissionDTO dto = new GroupPermissionDTO(groupIds, userId);
        return groupServiceClient.canWrite(dto);
    }

    private boolean isAdmin() {
        Set<GrantedAuthority> authoritySet = SecurityUtils.getCurrentAuthoritySet()
                .orElseThrow(PermissionException::new);
        return authoritySet.contains(AuthorityType.ADMIN.getGrantedAuthority());
    }

    private String getUserId() {
        String jwt = SecurityUtils.getCurrentJwt()
                .orElseThrow(PermissionException::new);
        return jwtTokenProvider.getUserIdFromJwt(jwt);
    }

}
