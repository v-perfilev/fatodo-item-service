package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final GroupService groupService;
    private final ItemService itemService;

    public List<UUID> getUserIdsByGroupId(UUID groupId) {
        Group group = groupService.getById(groupId);
        return group.getMembers().stream()
                .map(Member::getId)
                .collect(Collectors.toList());
    }

    public List<UUID> getUserIdsByItemId(UUID itemId) {
        Item item = itemService.getById(itemId);
        return getUserIdsByGroupId(item.getGroupId());
    }

}
