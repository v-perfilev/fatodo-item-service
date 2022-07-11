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
import org.springframework.transaction.annotation.Transactional;

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

    public void checkMultipleReadPermission(List<UUID> groupIdList) {
        boolean hasPermission = hasMultipleReadPermission(groupIdList);
        if (!hasPermission) {
            throw new PermissionException();
        }
    }

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

    public boolean hasMultipleReadPermission(List<UUID> groupIdList) {
        return checkMultiplePermission(groupIdList, permissionHelper::canRead);
    }

    public boolean hasMultipleEditPermission(List<UUID> groupIdList) {
        return checkMultiplePermission(groupIdList, permissionHelper::canEdit);
    }

    public boolean hasMultipleAdminPermission(List<UUID> groupIdList) {
        return checkMultiplePermission(groupIdList, permissionHelper::canAdmin);
    }

    public boolean hasReadItemPermission(UUID itemId) {
        return checkItemPermission(itemId, permissionHelper::canRead);
    }

    public boolean hasEditItemPermission(UUID itemId) {
        return checkItemPermission(itemId, permissionHelper::canEdit);
    }

    public boolean hasAdminItemPermission(UUID itemId) {
        return checkItemPermission(itemId, permissionHelper::canAdmin);
    }

    public boolean hasMultipleEditItemPermission(List<UUID> itemIdList) {
        return checkMultipleItemPermission(itemIdList, permissionHelper::canEdit);
    }

    public boolean hasMultipleAdminItemPermission(List<UUID> itemIdList) {
        return checkMultipleItemPermission(itemIdList, permissionHelper::canAdmin);
    }

    private boolean checkPermission(UUID groupId, Predicate<Group> checkGroup) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(ModelNotFoundException::new);
        return checkGroup.test(group);
    }

    private boolean checkMultiplePermission(List<UUID> groupIdList, Predicate<Group> checkGroup) {
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

    private boolean checkItemPermission(UUID itemId, Predicate<Group> checkGroup) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(ModelNotFoundException::new);
        Group group = item.getGroup();
        return checkGroup.test(group);
    }

    private boolean checkMultipleItemPermission(List<UUID> itemIdList, Predicate<Group> checkGroup) {
        itemIdList = itemIdList.stream()
                .distinct()
                .toList();
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
