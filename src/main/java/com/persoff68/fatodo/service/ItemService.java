package com.persoff68.fatodo.service;

import com.persoff68.fatodo.client.interceptor.GroupServiceClient;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.repository.ItemRepository;
import com.persoff68.fatodo.service.exception.ModelAlreadyExistsException;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final PermissionCheckService permissionCheckService;
    private final GroupServiceClient groupServiceClient;

    public List<Item> getAllByUserId(String userId) {
        Set<String> groupIdSet = groupServiceClient.getGroupIdsByUserId(userId);
        return itemRepository.findAllByGroupIdsContains(groupIdSet);
    }

    public List<Item> getAllByGroupId(String groupId) {
        permissionCheckService.checkReadPermission(groupId);
        Set<String> groupIdSet = Collections.singleton(groupId);
        return itemRepository.findAllByGroupIdsContains(groupIdSet);
    }

    public Item getById(String id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(ModelNotFoundException::new);
        permissionCheckService.checkReadPermission(item);
        return item;
    }

    public Item create(Item item) {
        String id = item.getId();
        if (id != null) {
            throw new ModelAlreadyExistsException();
        }
        permissionCheckService.checkWritePermission(item);
        return itemRepository.save(item);
    }

    public Item update(Item item) {
        String id = item.getId();
        if (!itemRepository.existsById(id)) {
            throw new ModelNotFoundException();
        }
        permissionCheckService.checkWritePermission(item);
        return itemRepository.save(item);
    }

    public void delete(String id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(ModelNotFoundException::new);
        permissionCheckService.checkWritePermission(item);
        itemRepository.deleteById(id);
    }


}
