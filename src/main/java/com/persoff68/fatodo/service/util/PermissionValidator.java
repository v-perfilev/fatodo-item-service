package com.persoff68.fatodo.service.util;

import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.service.exception.PermissionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class PermissionValidator {

    private final PermissionHelper permissionHelper;

    public void validateGetPermission(Set<String> groupIds) {
        boolean isValid = permissionHelper.canRead(groupIds);
        throwExceptionIfNotValid(isValid);
    }

    public void validateCreatePermission(Item item) {
        Set<String> groupIds = Set.of(item.getGroupId());
        boolean isValid = permissionHelper.canAdmin(groupIds);
        throwExceptionIfNotValid(isValid);
    }

    public void validateUpdatePermission(Item newItem, Item oldItem) {
        boolean isValid;
        if (!ItemUtils.areGroupIdsEquals(newItem, oldItem)) {
            Set<String> groupIds = Set.of(newItem.getGroupId(), oldItem.getGroupId());
            isValid = permissionHelper.canAdmin(groupIds);
        } else if (!ItemUtils.areStatusesEquals(newItem, oldItem)) {
            Set<String> groupIds = Set.of(newItem.getGroupId());
            isValid = permissionHelper.canAdmin(groupIds);
        } else {
            Set<String> groupIds = Set.of(newItem.getGroupId());
            isValid = permissionHelper.canEdit(groupIds);
        }
        throwExceptionIfNotValid(isValid);
    }

    public void validateDeletePermission(Item item) {
        Set<String> groupIds = Set.of(item.getGroupId());
        boolean isValid = permissionHelper.canAdmin(groupIds);
        throwExceptionIfNotValid(isValid);
    }

    private void throwExceptionIfNotValid(boolean valid) {
        if (!valid) {
            throw new PermissionException();
        }
    }

}
