package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.repository.ItemRepository;
import com.persoff68.fatodo.service.exception.ModelAlreadyExistsException;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import com.persoff68.fatodo.service.util.ItemUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final PermissionService permissionService;

    public List<Item> getAllByGroupIds(Set<String> groupIds) {
        permissionService.checkRead(groupIds);
        return itemRepository.findAllByGroupIdIn(groupIds);
    }

    public Item getById(String id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.checkRead(item.getGroupId());
        return item;
    }

    public Item create(Item item) {
        if (item.getId() != null) {
            throw new ModelAlreadyExistsException();
        }
        permissionService.checkAdmin(item.getGroupId());
        return itemRepository.save(item);
    }

    public Item update(Item item) {
        Item oldItem = itemRepository.findById(item.getId())
                .orElseThrow(ModelNotFoundException::new);

        if (!ItemUtils.areGroupIdsEquals(item, oldItem)) {
            permissionService.checkAdmin(Set.of(item.getGroupId(), oldItem.getGroupId()));
        } else if (!ItemUtils.areStatusesEquals(item, oldItem)) {
            permissionService.checkAdmin(item.getGroupId());
        } else {
            permissionService.checkEdit(item.getGroupId());
        }

        return itemRepository.save(item);
    }

    public void delete(String id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.checkAdmin(item.getGroupId());
        itemRepository.deleteById(id);
    }

}
