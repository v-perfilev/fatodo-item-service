package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.constant.ElementType;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.repository.ItemRepository;
import com.persoff68.fatodo.service.exception.ModelNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CheckService {

    private final GroupRepository groupRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public Pair<ElementType, UUID> getTypeAndParentId(UUID id) {
        Optional<Group> groupOptional = groupRepository.findById(id);
        if (groupOptional.isPresent()) {
            UUID parentId = groupOptional.get().getId();
            return Pair.of(ElementType.GROUP, parentId);
        }
        Optional<Item> itemOptional = itemRepository.findById(id);
        if (itemOptional.isPresent()) {
            UUID parentId = itemOptional.get().getGroup().getId();
            return Pair.of(ElementType.ITEM, parentId);
        }
        throw new ModelNotFoundException();
    }

}
