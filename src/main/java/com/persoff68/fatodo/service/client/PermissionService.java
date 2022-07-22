package com.persoff68.fatodo.service.client;

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

    public void checkGroupsPermission(UUID userId, Permission permission, List<UUID> groupIdList) {
        boolean hasPermission = hasGroupsPermission(userId, permission, groupIdList);
        if (!hasPermission) {
            throw new PermissionException();
        }
    }

    public void checkGroupPermission(UUID userId, Permission permission, UUID groupId) {
        List<UUID> groupIdList = Collections.singletonList(groupId);
        boolean hasPermission = hasGroupsPermission(userId, permission, groupIdList);
        if (!hasPermission) {
            throw new PermissionException();
        }
    }

    public void checkItemPermission(UUID userId, Permission permission, UUID itemId) {
        List<UUID> itemIdList = Collections.singletonList(itemId);
        boolean hasPermission = hasItemsPermission(userId, permission, itemIdList);
        if (!hasPermission) {
            throw new PermissionException();
        }
    }

    public boolean hasGroupsPermission(UUID userId, Permission permission, List<UUID> groupIdList) {
        Predicate<Group> checkPredicate = getCheckPredicateByPermissionType(userId, permission);
        List<Group> groupList = prepareGroupListForCheck(groupIdList);
        return groupList.stream().map(checkPredicate::test).allMatch(Predicate.isEqual(true));
    }

    public List<UUID> getAllowedGroupsIds(UUID userId, Permission permission, List<UUID> groupIdList) {
        Predicate<Group> checkPredicate = getCheckPredicateByPermissionType(userId, permission);
        List<Group> groupList = prepareGroupListForCheck(groupIdList);
        return groupList.stream().filter(checkPredicate).map(Group::getId).toList();
    }

    public boolean hasItemsPermission(UUID userId, Permission permission, List<UUID> itemIdList) {
        Predicate<Group> checkPredicate = getCheckPredicateByPermissionType(userId, permission);
        List<Item> itemList = prepareItemListForCheck(itemIdList);
        List<Group> groupList = itemList.stream().map(Item::getGroup).distinct().toList();
        return groupList.stream().map(checkPredicate::test).allMatch(Predicate.isEqual(true));
    }

    public List<UUID> getAllowedItemIds(UUID userId, Permission permission, List<UUID> itemIdList) {
        Predicate<Group> checkPredicate = getCheckPredicateByPermissionType(userId, permission);
        List<Item> itemList = prepareItemListForCheck(itemIdList);
        List<Group> groupList = itemList.stream().map(Item::getGroup).distinct().toList();
        List<Group> allowedGroupList = groupList.stream().filter(checkPredicate).toList();
        return itemList.stream()
                .filter(item -> allowedGroupList.contains(item.getGroup()))
                .map(Item::getId)
                .toList();
    }

    private List<Group> prepareGroupListForCheck(List<UUID> groupIdList) {
        groupIdList = groupIdList.stream().distinct().toList();
        List<Group> groupList = groupRepository.findAllById(groupIdList);
        if (groupList.size() != groupIdList.size()) {
            throw new ModelNotFoundException();
        }
        return groupList;
    }

    private List<Item> prepareItemListForCheck(List<UUID> itemIdList) {
        itemIdList = itemIdList.stream().distinct().toList();
        List<Item> itemList = itemRepository.findAllByIds(itemIdList);
        if (itemList.size() != itemIdList.size()) {
            throw new ModelNotFoundException();
        }
        return itemList;
    }

    private Predicate<Group> getCheckPredicateByPermissionType(UUID userId, Permission permission) {
        return switch (permission) {
            case READ -> group -> permissionHelper.canRead(userId, group);
            case EDIT -> group -> permissionHelper.canEdit(userId, group);
            case ADMIN -> group -> permissionHelper.canAdmin(userId, group);
        };
    }

}
