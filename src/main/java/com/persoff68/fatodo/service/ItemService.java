package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.constant.ItemStatus;
import com.persoff68.fatodo.repository.ItemRepository;
import com.persoff68.fatodo.service.exception.ModelAlreadyExistsException;
import com.persoff68.fatodo.service.exception.ModelInvalidException;
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

    public Item create(Item item) {
        if (item.getId() != null) {
            throw new ModelAlreadyExistsException();
        }
        permissionValidator.validateCreate(item);

        item.setStatus(ItemStatus.ACTIVE);

        return itemRepository.save(item);
    }

    public Item update(Item newItem) {
        if (newItem.getId() == null) {
            throw new ModelInvalidException();
        }
        Item item = itemRepository.findById(newItem.getId())
                .orElseThrow(ModelNotFoundException::new);
        permissionValidator.validateUpdate(item);

        item.setTitle(newItem.getTitle());
        item.setType(newItem.getType());
        item.setPriority(newItem.getPriority());
        item.setDate(newItem.getDate());
        item.setDescription(newItem.getDescription());
        item.setReminders(item.getReminders());
        item.setTags(item.getTags());

        return itemRepository.save(item);
    }

    public void delete(String id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(ModelNotFoundException::new);
        permissionValidator.validateDelete(item);
        itemRepository.delete(item);
    }

}
