package com.persoff68.fatodo.service;

import com.persoff68.fatodo.client.GroupServiceClient;
import com.persoff68.fatodo.config.aop.cache.annotation.RedisCacheEvict;
import com.persoff68.fatodo.config.aop.cache.annotation.RedisCacheable;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.repository.ItemRepository;
import com.persoff68.fatodo.service.exception.ModelAlreadyExistsException;
import com.persoff68.fatodo.service.exception.ModelInvalidException;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import com.persoff68.fatodo.service.validator.PermissionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {

    @Resource
    private ItemService itemService;

    private final ItemRepository itemRepository;
    private final PermissionValidator permissionValidator;
    private final GroupServiceClient groupServiceClient;

    public List<Item> getAllForUser() {
        List<String> groupIdList = groupServiceClient.getAllGroupIdsForUser();
        List<Item> itemList = new ArrayList<>();
        for (String groupId : groupIdList) {
            itemList.addAll(itemService.getAllByGroupId(groupId));
        }
        return itemList;
    }

    @RedisCacheable(cacheName = "items", key = "#groupId")
    public List<Item> getAllByGroupId(String groupId) {
        permissionValidator.validateGet(List.of(groupId));
        return itemRepository.findAllByGroupId(groupId);
    }

    public Item getById(String id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(ModelNotFoundException::new);
        permissionValidator.validateGet(List.of(item.getGroupId()));
        return item;
    }

    @RedisCacheEvict(cacheName = "items", key = "#item.groupId")
    public Item create(Item item) {
        if (item.getId() != null) {
            throw new ModelAlreadyExistsException();
        }
        permissionValidator.validateCreate(item);
        return itemRepository.save(item);
    }

    @RedisCacheEvict(cacheName = "items", key = "#item.groupId")
    public Item update(Item item) {
        if (item.getId() == null) {
            throw new ModelInvalidException();
        }
        Item oldItem = itemRepository.findById(item.getId())
                .orElseThrow(ModelNotFoundException::new);
        permissionValidator.validateUpdate(item, oldItem);
        return itemRepository.save(item);
    }

    public void deleteById(String id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(ModelNotFoundException::new);
        itemService.delete(item);
    }

    @RedisCacheEvict(cacheName = "items", key = "#item.groupId")
    public void delete(Item item) {
        permissionValidator.validateDelete(item);
        itemRepository.delete(item);
    }

    public int getCountByGroupId(String groupId) {
        permissionValidator.validateGet(List.of(groupId));
        return itemRepository.findAllByGroupId(groupId).size();
    }

}
