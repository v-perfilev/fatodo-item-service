package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.repository.ItemRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import com.persoff68.fatodo.service.exception.PermissionException;
import com.persoff68.fatodo.service.helper.PermissionHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class PermissionService {

    private final GroupRepository groupRepository;
    private final ItemRepository itemRepository;
    private final PermissionHelper permissionHelper;

    public void checkReadPermission(UUID groupId) {
        boolean hasPermission = hasReadPermission(groupId);
        if (!hasPermission) {
            throw new PermissionException();
        }
    }

    public void checkEditPermission(UUID groupId) {
        boolean hasPermission = hasEditPermission(groupId);
        if (!hasPermission) {
            throw new PermissionException();
        }
    }

    public void checkAdminPermission(UUID groupId) {
        boolean hasPermission = hasAdminPermission(groupId);
        if (!hasPermission) {
            throw new PermissionException();
        }
    }

    public void checkReadItemPermission(UUID itemId) {
        boolean hasPermission = hasReadItemPermission(itemId);
        if (!hasPermission) {
            throw new PermissionException();
        }
    }

    public boolean hasReadPermission(UUID groupId) {
        return checkPermission(groupId, permissionHelper::canRead);
    }

    public boolean hasEditPermission(UUID groupId) {
        return checkPermission(groupId, permissionHelper::canEdit);
    }

    public boolean hasAdminPermission(UUID groupId) {
        return checkPermission(groupId, permissionHelper::canAdmin);
    }


    public boolean hasReadItemPermission(UUID itemId) {
        return checkItemPermission(itemId, permissionHelper::canRead);
    }

    private boolean checkPermission(UUID groupId, Predicate<Group> checkGroup) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(ModelNotFoundException::new);
        return checkGroup.test(group);
    }

    private boolean checkItemPermission(UUID itemId, Predicate<Group> checkGroup) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(ModelNotFoundException::new);
        Group group = groupRepository.findById(item.getGroupId())
                .orElseThrow(ModelNotFoundException::new);
        return checkGroup.test(group);
    }

}
