package com.persoff68.fatodo.service;

import com.persoff68.fatodo.client.CommentServiceClient;
import com.persoff68.fatodo.client.NotificationServiceClient;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.PageableList;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.repository.ItemRepository;
import com.persoff68.fatodo.repository.OffsetPageRequest;
import com.persoff68.fatodo.service.client.EventService;
import com.persoff68.fatodo.service.client.PermissionService;
import com.persoff68.fatodo.service.client.WsService;
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

    private final PermissionService permissionService;
    private final EventService eventService;
    private final WsService wsService;
    private final GroupRepository groupRepository;
    private final ItemRepository itemRepository;
    private final CommentServiceClient commentServiceClient;
    private final NotificationServiceClient notificationServiceClient;

    public Map<UUID, PageableList<Item>> getMapByGroupIds(UUID userId, List<UUID> groupIdList, int size) {
        permissionService.checkGroupsPermission(userId, Permission.READ, groupIdList);
        return groupIdList.stream().distinct().collect(Collectors.toMap(Function.identity(),
                groupId -> getPageableListForMap(groupId, size)));
    }

    public PageableList<Item> getAllByGroupId(UUID userId, UUID groupId, Pageable pageable) {
        permissionService.checkGroupPermission(userId, Permission.READ, groupId);
        Page<Item> itemPage = itemRepository.findAllByGroupIdPageable(groupId, pageable);
        return PageableList.of(itemPage.getContent(), itemPage.getTotalElements());
    }

    public PageableList<Item> getAllArchivedByGroupId(UUID userId, UUID groupId, Pageable pageable) {
        permissionService.checkGroupPermission(userId, Permission.READ, groupId);
        Page<Item> itemPage = itemRepository.findAllArchivedByGroupIdPageable(groupId, pageable);
        return PageableList.of(itemPage.getContent(), itemPage.getTotalElements());
    }

    public Item getByIdWithoutPermissionCheck(UUID itemId) {
        return itemRepository.findById(itemId).orElseThrow(ModelNotFoundException::new);
    }

    @Transactional
    public List<Item> getAllByIds(UUID userId, List<UUID> itemIdList) {
        List<Item> itemList = itemRepository.findAllById(itemIdList);
        List<Group> allowedGroupList = itemList.stream()
                .map(Item::getGroup)
                .distinct()
                .filter(g -> g.getMembers().stream().map(Member::getUserId).anyMatch(userId::equals))
                .toList();
        return itemList.stream()
                .filter(i -> allowedGroupList.contains(i.getGroup()))
                .toList();
    }

    public Item getById(UUID userId, UUID itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(ModelNotFoundException::new);
        permissionService.checkItemPermission(userId, Permission.READ, item.getId());
        return item;
    }

    @Transactional
    public Item createFirstStep(UUID userId, Item newItem, UUID groupId) {
        if (newItem.getId() != null) {
            throw new ModelAlreadyExistsException();
        }
        Group group = groupRepository.findById(groupId).orElseThrow(ModelNotFoundException::new);

        permissionService.checkGroupPermission(userId, Permission.EDIT, group.getId());

        newItem.setGroup(group);

        return itemRepository.save(newItem);
    }

    public Item createSecondStep(UUID userId, UUID itemId, List<Reminder> reminderList) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(ModelNotFoundException::new);

        try {

            if (reminderList != null) {
                notificationServiceClient.setReminders(item.getId(), reminderList);
                item.setRemindersCount(reminderList.size());
            }

            item = itemRepository.save(item);

            // EVENT
            eventService.sendItemCreateEvent(item, userId);
            // WS
            wsService.sendItemCreateEvent(item, userId);

            return item;
        } catch (Exception e) {
            itemRepository.deleteById(itemId);
            throw e;
        }

    }

    @Transactional
    public Item update(UUID userId, Item newItem, List<Reminder> reminderList, boolean deleteReminders) {
        UUID id = newItem.getId();
        if (id == null) {
            throw new ModelInvalidException();
        }
        Item item = itemRepository.findById(id).orElseThrow(ModelNotFoundException::new);
        permissionService.checkItemPermission(userId, Permission.EDIT, item.getId());

        item.setTitle(newItem.getTitle());
        item.setPriority(newItem.getPriority());
        item.setDescription(newItem.getDescription());
        item.setDone(newItem.isDone());

        if (reminderList != null) {
            notificationServiceClient.setReminders(item.getId(), reminderList);
            item.setRemindersCount(reminderList.size());
        } else if (deleteReminders) {
            notificationServiceClient.deleteRemindersByTargetId(item.getId());
            item.setRemindersCount(0);
        }

        itemRepository.save(item);

        // EVENT
        eventService.sendItemUpdateEvent(item, userId);
        // WS
        wsService.sendItemUpdateEvent(item, userId);

        return item;
    }

    @Transactional
    public Item updateStatus(UUID userId, UUID itemId, boolean isDone) {
        Item item = itemRepository.findById(itemId).orElseThrow(ModelNotFoundException::new);
        permissionService.checkItemPermission(userId, Permission.EDIT, item.getId());
        item.setDone(isDone);
        item = itemRepository.save(item);

        // EVENT
        eventService.sendItemUpdateStatusEvent(item, userId);
        // WS
        wsService.sendItemUpdateStatusEvent(item, userId);

        return item;
    }

    @Transactional
    public Item updateArchived(UUID userId, UUID itemId, boolean archived) {
        Item item = itemRepository.findById(itemId).orElseThrow(ModelNotFoundException::new);
        permissionService.checkItemPermission(userId, Permission.EDIT, item.getId());
        item.setArchived(archived);
        if (archived) {
            notificationServiceClient.deleteRemindersByTargetId(itemId);
        }
        item = itemRepository.save(item);

        // EVENT
        eventService.sendItemUpdateArchivedEvent(item, userId);
        // WS
        wsService.sendItemUpdateArchivedEvent(item, userId);

        return item;
    }

    @Transactional
    public void delete(UUID userId, UUID itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(ModelNotFoundException::new);
        permissionService.checkItemPermission(userId, Permission.EDIT, item.getId());

        commentServiceClient.deleteThreadByTargetId(itemId);
        notificationServiceClient.deleteRemindersByTargetId(itemId);

        item.setDeleted(true);
        itemRepository.save(item);

        // EVENT
        eventService.sendItemDeleteEvent(item, userId);
        // WS
        wsService.sendItemDeleteEvent(item, userId);
    }

    private PageableList<Item> getPageableListForMap(UUID groupId, int size) {
        Pageable requestPage = OffsetPageRequest.of(0, size);
        Page<Item> itemPage = itemRepository.findAllByGroupIdPageable(groupId, requestPage);
        return PageableList.of(itemPage.getContent(), itemPage.getTotalElements());
    }

}
