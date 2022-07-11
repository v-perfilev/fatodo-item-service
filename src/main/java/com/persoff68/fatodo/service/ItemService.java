package com.persoff68.fatodo.service;

import com.persoff68.fatodo.client.CommentServiceClient;
import com.persoff68.fatodo.client.NotificationServiceClient;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.PageableList;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.constant.ItemStatus;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.repository.ItemRepository;
import com.persoff68.fatodo.repository.OffsetPageRequest;
import com.persoff68.fatodo.service.exception.ModelAlreadyExistsException;
import com.persoff68.fatodo.service.exception.ModelInvalidException;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final GroupRepository groupRepository;

    private final ItemRepository itemRepository;
    private final PermissionService permissionService;
    private final CommentServiceClient commentServiceClient;
    private final NotificationServiceClient notificationServiceClient;

    public Map<UUID, PageableList<Item>> getMapByGroupIds(List<UUID> groupIdList, int size) {
        permissionService.checkMultipleReadPermission(groupIdList);
        return groupIdList.stream()
                .distinct()
                .collect(Collectors.toMap(Function.identity(), groupId -> getPageableListForMap(groupId, size)));
    }

    public PageableList<Item> getAllByGroupId(UUID groupId, Pageable pageable) {
        permissionService.checkReadPermission(groupId);
        Page<Item> itemPage = itemRepository.findAllByGroupIdPageable(groupId, pageable);
        return PageableList.of(itemPage.getContent(), itemPage.getTotalElements());
    }

    public PageableList<Item> getAllArchivedByGroupId(UUID groupId, Pageable pageable) {
        permissionService.checkReadPermission(groupId);
        Page<Item> itemPage = itemRepository.findAllArchivedByGroupIdPageable(groupId, pageable);
        return PageableList.of(itemPage.getContent(), itemPage.getTotalElements());
    }

    public Item getByIdWithoutPermissionCheck(UUID itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(ModelNotFoundException::new);
    }

    public Item getById(UUID itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.checkReadPermission(item.getGroup().getId());
        return item;
    }

    @Transactional
    public Item create(Item newItem, UUID groupId, List<Reminder> reminderList) {
        if (newItem.getId() != null) {
            throw new ModelAlreadyExistsException();
        }
        Group group = groupRepository.findById(groupId)
                .orElseThrow(ModelNotFoundException::new);

        permissionService.checkEditPermission(group.getId());

        newItem.setStatus(ItemStatus.CREATED);
        newItem.setGroup(group);

        Item item = itemRepository.save(newItem);
        if (reminderList != null) {
            notificationServiceClient.setReminders(item.getId(), reminderList);
        }
        return item;
    }

    @Transactional
    public Item update(Item newItem, List<Reminder> reminderList, boolean deleteReminders) {
        UUID id = newItem.getId();
        if (id == null) {
            throw new ModelInvalidException();
        }
        Item item = itemRepository.findById(id)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.checkEditPermission(item.getGroup().getId());

        item.setTitle(newItem.getTitle());
        item.setType(newItem.getType());
        item.setPriority(newItem.getPriority());
        item.setDate(newItem.getDate());
        item.setDescription(newItem.getDescription());

        itemRepository.save(item);
        if (reminderList != null) {
            notificationServiceClient.setReminders(item.getId(), reminderList);
        } else if (deleteReminders) {
            notificationServiceClient.deleteRemindersByTargetId(item.getId());
        }
        return item;
    }

    @Transactional
    public Item updateStatus(UUID itemId, ItemStatus status) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.checkEditPermission(item.getGroup().getId());
        item.setStatus(status);
        return itemRepository.save(item);
    }

    @Transactional
    public Item updateArchived(UUID itemId, boolean archived) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.checkEditPermission(item.getGroup().getId());
        item.setArchived(archived);
        if (archived) {
            notificationServiceClient.deleteRemindersByTargetId(itemId);
        }
        return itemRepository.save(item);
    }

    @Transactional
    public void delete(UUID itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(ModelNotFoundException::new);
        permissionService.checkEditPermission(item.getGroup().getId());

        // TODO check and optimize
        commentServiceClient.deleteThreadByTargetId(itemId);
        notificationServiceClient.deleteRemindersByTargetId(itemId);

        item.setDeleted(true);
        itemRepository.save(item);
    }

    private PageableList<Item> getPageableListForMap(UUID groupId, int size) {
        Pageable requestPage = OffsetPageRequest.of(0, size);
        Page<Item> itemPage = itemRepository.findAllByGroupIdPageable(groupId, requestPage);
        return PageableList.of(itemPage.getContent(), itemPage.getTotalElements());
    }

}
