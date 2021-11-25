package com.persoff68.fatodo.service;

import com.persoff68.fatodo.client.CommentServiceClient;
import com.persoff68.fatodo.client.NotificationServiceClient;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.constant.ItemStatus;
import com.persoff68.fatodo.repository.ItemRepository;
import com.persoff68.fatodo.service.exception.ModelAlreadyExistsException;
import com.persoff68.fatodo.service.exception.ModelInvalidException;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final PermissionService permissionService;
    private final CommentServiceClient commentServiceClient;
    private final NotificationServiceClient notificationServiceClient;

    public List<Item> getAllByGroupId(UUID groupId) {
        permissionService.checkReadPermission(groupId);
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        return itemRepository.findAllByGroupId(groupId, sort);
    }

    public Item getByIdWithoutPermissionCheck(UUID id) {
        return itemRepository.findById(id)
                .orElseThrow(ModelNotFoundException::new);
    }

    public Item getById(UUID id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.checkReadPermission(item.getGroupId());
        return item;
    }

    public Item create(Item newItem, List<Reminder> reminderList) {
        if (newItem.getId() != null) {
            throw new ModelAlreadyExistsException();
        }
        permissionService.checkEditPermission(newItem.getGroupId());

        newItem.setStatus(ItemStatus.ACTIVE);

        Item item = itemRepository.save(newItem);
        if (reminderList != null) {
            notificationServiceClient.setReminders(item.getId(), reminderList);
        }
        return item;
    }

    public Item update(Item newItem, List<Reminder> reminderList, boolean deleteReminders) {
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
        item.setTags(newItem.getTags());

        itemRepository.save(item);
        if (reminderList != null) {
            notificationServiceClient.setReminders(item.getId(), reminderList);
        } else if (deleteReminders) {
            notificationServiceClient.deleteReminders(item.getId());
        }
        return item;
    }

    public void delete(UUID id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.checkEditPermission(item.getGroupId());
        List<UUID> idList = Collections.singletonList(item.getId());
        commentServiceClient.deleteAllThreadsByTargetIds(idList);
        notificationServiceClient.deleteReminders(id);
        itemRepository.delete(item);
    }

    public void deleteAllByGroupId(UUID groupId) {
        permissionService.checkAdminPermission(groupId);
        Sort sort = Sort.unsorted();
        List<UUID> itemIdList = itemRepository.findAllByGroupId(groupId, sort)
                .stream().map(Item::getId).collect(Collectors.toList());
        commentServiceClient.deleteAllThreadsByTargetIds(itemIdList);
        itemIdList.forEach(notificationServiceClient::deleteReminders);
        itemRepository.deleteAllByIdInAndGroupId(itemIdList, groupId);
    }

}
