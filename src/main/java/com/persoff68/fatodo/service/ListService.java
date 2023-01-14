package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.PageableList;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ListService {

    private final GroupRepository groupRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public PageableList<Item> getAllPageable(UUID userId, Pageable pageable) {
        List<Group> groupList = groupRepository.findAllByUserId(userId);
        List<UUID> groupIdList = groupList.stream().map(Group::getId).toList();
        Page<Item> itemPage = itemRepository.findAllByGroupIdsPageable(groupIdList, pageable);
        return PageableList.of(itemPage.getContent(), itemPage.getTotalElements());
    }

}
