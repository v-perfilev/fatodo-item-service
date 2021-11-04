package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.ReminderMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReminderService {
    private final static String ITEM_URL_PREFIX = "/items/";

    private final ItemService itemService;
    private final GroupService groupService;

    public ReminderMessage generateByItemId(UUID itemId) {
        Item item = itemService.getByIdWithoutPermissionCheck(itemId);
        Group group = groupService.getByIdWithoutPermissionCheck(item.getGroupId());
        List<UUID> userIdList = getUserIdsFromGroup(group);
        String message = createItemMessage(item);
        String url = createItemUrl(item);
        return new ReminderMessage(message, url, userIdList);
    }

    private String createItemMessage(Item item) {
        return item.getTitle();
    }

    private String createItemUrl(Item item) {
        return ITEM_URL_PREFIX + item.getId();
    }

    private List<UUID> getUserIdsFromGroup(Group group) {
        return group.getMembers().stream()
                .map(Member::getId)
                .collect(Collectors.toList());
    }

}
