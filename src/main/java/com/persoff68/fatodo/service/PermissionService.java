package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.repository.ItemRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import com.persoff68.fatodo.service.exception.PermissionException;
import com.persoff68.fatodo.service.helper.PermissionHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
@Transactional
public class PermissionService {

    private final GroupRepository groupRepository;
    private final ItemRepository itemRepository;
    private final PermissionHelper permissionHelper;

    public void checkGroupsPermission(Permission permission, List<UUID> groupIdList) {
        boolean hasPermission = hasGroupsPermission(permission, groupIdList);
        if (!hasPermission) {
            throw new PermissionException();
        }
    }

    public void checkGroupPermission(Permission permission, UUID groupId) {
        List<UUID> groupIdList = Collections.singletonList(groupId);
        boolean hasPermission = hasGroupsPermission(permission, groupIdList);
        if (!hasPermission) {
            throw new PermissionException();
        }
    }

    public void checkItemsPermission(Permission permission, List<UUID> itemIdList) {
        boolean hasPermission = hasItemsPermission(permission, itemIdList);
        if (!hasPermission) {
            throw new PermissionException();
        }
    }

    public void checkItemPermission(Permission permission, UUID itemId) {
        List<UUID> itemIdList = Collections.singletonList(itemId);
        boolean hasPermission = hasItemsPermission(permission, itemIdList);
        if (!hasPermission) {
            throw new PermissionException();
        }
    }

    public boolean hasGroupsPermission(Permission permission, List<UUID> groupIdList) {
        Predicate<Group> checkPredicate = switch (permission) {
            case READ -> permissionHelper::canRead;
            case EDIT -> permissionHelper::canEdit;
            case ADMIN -> permissionHelper::canAdmin;
        };
        return hasMultipleGroupPermission(groupIdList, checkPredicate);
    }

    public boolean hasItemsPermission(Permission permission, List<UUID> itemIdList) {
        Predicate<Group> checkPredicate = switch (permission) {
            case READ -> permissionHelper::canRead;
            case EDIT -> permissionHelper::canEdit;
            case ADMIN -> permissionHelper::canAdmin;
        };
        return hasMultipleItemPermission(itemIdList, checkPredicate);
    }

    private boolean hasMultipleGroupPermission(List<UUID> groupIdList, Predicate<Group> checkGroup) {
        groupIdList = groupIdList.stream()
                .distinct()
                .toList();
        List<Group> groupList = groupRepository.findAllById(groupIdList);
        if (groupList.size() != groupIdList.size()) {
            throw new ModelNotFoundException();
        }
        return groupList.stream()
                .map(checkGroup::test)
                .allMatch(Predicate.isEqual(true));
    }

    private boolean hasMultipleItemPermission(List<UUID> itemIdList, Predicate<Group> checkGroup) {
        itemIdList = itemIdList.stream().distinct().toList();
        List<Item> itemList = itemRepository.findAllByIds(itemIdList);
        if (itemList.size() != itemIdList.size()) {
            throw new ModelNotFoundException();
        }
        List<UUID> groupIdList = itemList.stream()
                .map(Item::getGroup)
                .map(Group::getId)
                .distinct()
                .toList();
        List<Group> groupList = groupRepository.findAllById(groupIdList);
        return groupList.stream()
                .map(checkGroup::test)
                .allMatch(Predicate.isEqual(true));
    }

}
