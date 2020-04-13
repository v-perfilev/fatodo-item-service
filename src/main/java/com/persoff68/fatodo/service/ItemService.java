package com.persoff68.fatodo.service;

import com.persoff68.fatodo.client.GroupServiceClient;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.repository.ItemRepository;
import com.persoff68.fatodo.service.exception.ModelAlreadyExistsException;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import com.persoff68.fatodo.service.validator.PermissionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final PermissionValidator permissionValidator;
    private final GroupServiceClient groupServiceClient;

    public List<Item> getAllForUser() {
        List<String> groupIdList = groupServiceClient.getAllGroupIdsForUser();
        return itemRepository.findAllByGroupIdIn(groupIdList);
    }

    public List<Item> getAllByGroupIds(List<String> groupIdList) {
        permissionValidator.validateGetPermission(groupIdList);
        return itemRepository.findAllByGroupIdIn(groupIdList);
    }

    public Item getById(String id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(ModelNotFoundException::new);
        permissionValidator.validateGetPermission(List.of(item.getGroupId()));
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

    public void deleteById(String id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(ModelNotFoundException::new);
        permissionValidator.validateDeletePermission(item);
        itemRepository.deleteById(id);
    }

}
