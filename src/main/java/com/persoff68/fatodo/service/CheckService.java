package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckService {

    private final GroupRepository groupRepository;
    private final ItemRepository itemRepository;

    public boolean isGroup(UUID id) {
        Optional<Group> groupOptional = groupRepository.findById(id);
        return groupOptional.isPresent();
    }

    public boolean isItem(UUID id) {
        Optional<Item> itemOptional = itemRepository.findById(id);
        return itemOptional.isPresent();
    }
}
