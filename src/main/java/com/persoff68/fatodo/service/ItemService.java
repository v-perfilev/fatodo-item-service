package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.repository.ItemRepository;
import com.persoff68.fatodo.service.exception.ModelAlreadyExistsException;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import com.persoff68.fatodo.service.util.ItemUtils;
import com.persoff68.fatodo.service.util.PermissionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final PermissionValidator permissionValidator;

    public List<Item> getAllByGroupIds(Set<String> groupIds) {
        permissionValidator.validateGetPermission(groupIds);
        return itemRepository.findAllByGroupIdIn(groupIds);
    }

    public Item getById(String id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(ModelNotFoundException::new);
        permissionValidator.validateGetPermission(Set.of(item.getGroupId()));
        return item;
    }

    public Item create(Item item) {
        if (item.getId() != null) {
            throw new ModelAlreadyExistsException();
        }
        permissionValidator.validateCreatePermission(item);
        return itemRepository.save(item);
    }

    public Item update(Item item) {
        Item oldItem = itemRepository.findById(item.getId())
                .orElseThrow(ModelNotFoundException::new);
        permissionValidator.validateUpdatePermission(item, oldItem);
        return itemRepository.save(item);
    }

    public void delete(String id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(ModelNotFoundException::new);
        permissionValidator.validateDeletePermission(item);
        itemRepository.deleteById(id);
    }

}
