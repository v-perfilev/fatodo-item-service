package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final GroupService groupService;
    private final ItemService itemService;

    public List<UUID> getGroupUserIdsById(UUID groupId) {
        Group group = groupService.getById(groupId);
        return group.getUsers().stream()
                .map(Group.User::getId)
                .collect(Collectors.toList());
    }

    public List<UUID> getItemUserIdsById(UUID itemId) {
        Item item = itemService.getById(itemId);
        return getGroupUserIdsById(item.getGroupId());
    }

}
