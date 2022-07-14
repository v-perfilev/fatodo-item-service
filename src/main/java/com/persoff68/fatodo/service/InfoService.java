package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.ReminderMailInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InfoService {
    private static final String ITEM_URL_PREFIX = "/items/";

    private final ItemService itemService;
    private final GroupService groupService;

    public ReminderMailInfo generateReminderMailInfo(UUID itemId) {
        Item item = itemService.getByIdWithoutPermissionCheck(itemId);
        Group group = groupService.getByIdWithoutPermissionCheck(item.getGroup().getId());
        List<UUID> userIdList = getUserIdsFromGroup(group);
        String message = createItemMessage(item);
        String url = createItemUrl(item);
        return new ReminderMailInfo(message, url, userIdList);
    }

    private String createItemMessage(Item item) {
        return item.getTitle();
    }

    private String createItemUrl(Item item) {
        return ITEM_URL_PREFIX + item.getId();
    }

    private List<UUID> getUserIdsFromGroup(Group group) {
        return group.getMembers().stream()
                .map(Member::getUserId)
                .toList();
    }

}
