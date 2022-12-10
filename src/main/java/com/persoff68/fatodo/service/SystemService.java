package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.repository.ConfigurationRepository;
import com.persoff68.fatodo.repository.GroupRepository;
import com.persoff68.fatodo.repository.ItemRepository;
import com.persoff68.fatodo.service.client.ImageService;
import com.persoff68.fatodo.service.exception.GroupInvalidException;
import com.persoff68.fatodo.service.validator.GroupValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SystemService {

    private final ImageService imageService;
    private final ConfigurationRepository configurationRepository;
    private final GroupRepository groupRepository;
    private final ItemRepository itemRepository;
    private final GroupValidator groupValidator;

    @Transactional
    public List<Item> getAllItemsByIds(List<UUID> itemIdList) {
        return itemRepository.findAllById(itemIdList);
    }

    @Transactional
    public void deleteAccountPermanently(UUID userId) {
        configurationRepository.deleteByUserId(userId);

        List<Group> groupList = groupRepository.findAllByUserId(userId);
        groupList.forEach(group -> deleteUserFromGroup(group, userId));
    }

    private void deleteUserFromGroup(Group group, UUID userId) {
        List<Member> memberList = group.getMembers();
        if (memberList.size() == 1) {
            imageService.deleteGroup(group);
            groupRepository.delete(group);
        } else {
            List<Member> memberToDeleteList = memberList.stream()
                    .filter(m -> m.getUserId().equals(userId))
                    .toList();
            memberList.removeAll(memberToDeleteList);
            try {
                groupValidator.validateUpdate(group);
            } catch (GroupInvalidException e) {
                memberList.get(0).setPermission(Permission.ADMIN);
            }
            groupRepository.save(group);
        }
    }

}
