package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.constant.ItemStatus;
import com.persoff68.fatodo.repository.ItemRepository;
import com.persoff68.fatodo.service.exception.ModelAlreadyExistsException;
import com.persoff68.fatodo.service.exception.ModelInvalidException;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final PermissionService permissionService;

    public List<Item> getAllByGroupId(UUID groupId) {
        permissionService.checkReadPermission(groupId);
        return itemRepository.findAllByGroupId(groupId);
    }

    public Item getById(UUID id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.checkReadPermission(item.getGroupId());
        return item;
    }

    public Item create(Item item) {
        if (item.getId() != null) {
            throw new ModelAlreadyExistsException();
        }
        permissionService.checkEditPermission(item.getGroupId());

        item.setStatus(ItemStatus.ACTIVE);

        return itemRepository.save(item);
    }

    public Item update(Item newItem) {
        if (newItem.getId() == null) {
            throw new ModelInvalidException();
        }
        Item item = itemRepository.findById(newItem.getId())
                .orElseThrow(ModelNotFoundException::new);
        permissionService.checkEditPermission(item.getGroupId());

        item.setTitle(newItem.getTitle());
        item.setType(newItem.getType());
        item.setPriority(newItem.getPriority());
        item.setDate(newItem.getDate());
        item.setDescription(newItem.getDescription());
        item.setReminders(newItem.getReminders());
        item.setTags(newItem.getTags());

        return itemRepository.save(item);
    }

    public void delete(UUID id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.checkAdminPermission(item.getGroupId());
        itemRepository.delete(item);
    }

    public void deleteAllByGroupId(UUID groupId) {
        permissionService.checkAdminPermission(groupId);
        List<UUID> idList = itemRepository.findAllByGroupId(groupId)
                .stream().map(Item::getId).collect(Collectors.toList());
        itemRepository.deleteAllByIdInAndGroupId(idList, groupId);
    }

}
