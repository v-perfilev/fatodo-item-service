package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.constant.ItemStatus;
import com.persoff68.fatodo.repository.ItemRepository;
import com.persoff68.fatodo.service.exception.ModelAlreadyExistsException;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import com.persoff68.fatodo.service.util.ItemUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final GroupPermissionService groupPermissionService;

    public List<Item> getAllByGroupIds(Set<String> groupIds) {
        groupPermissionService.checkReadPermission(groupIds);
        return itemRepository.findAllByGroupIdIn(groupIds);
    }

    public Item getById(String id) {
        Item item = itemRepository.findById(id).orElseThrow(ModelNotFoundException::new);
        groupPermissionService.checkReadPermission(item.getGroupId());
        return item;
    }

    public Item create(Item item) {
        if (item.getId() != null) {
            throw new ModelAlreadyExistsException();
        }
        groupPermissionService.checkAdminPermission(item.getGroupId());
        return itemRepository.save(item);
    }

    public Item update(Item item) {
        Item oldItem = itemRepository.findById(item.getId())
                .orElseThrow(ModelNotFoundException::new);

        if (ItemUtils.areGroupIdsEquals(item, oldItem)) {
            Set<String> groupIds = Set.of(item.getGroupId(), oldItem.getGroupId());
            groupPermissionService.checkAdminPermission(groupIds);
        } else if (ItemUtils.areStatusesEquals(item, oldItem)) {
            groupPermissionService.checkAdminPermission(item.getGroupId());
        } else {
            groupPermissionService.checkEditPermission(item.getGroupId());
        }

        return itemRepository.save(item);
    }

    public void delete(String id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(ModelNotFoundException::new);
        groupPermissionService.checkAdminPermission(item.getGroupId());
        itemRepository.deleteById(id);
    }


}
