package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Configuration;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.repository.ConfigurationRepository;
import com.persoff68.fatodo.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConfigurationService {

    private final ConfigurationRepository configurationRepository;
    private final GroupRepository groupRepository;

    public Configuration getByUserId(UUID userId) {
        return configurationRepository.findByUserId(userId)
                .orElse(new Configuration(userId));
    }

    @Transactional
    public void setOrder(UUID userId, List<UUID> groupIdList) {
        Configuration configuration = configurationRepository.findByUserId(userId)
                .orElse(new Configuration(userId));
        List<Group> groupList = groupRepository.findAllByUserId(userId);
        Map<UUID, Integer> orderMap = buildOrderMap(groupList, groupIdList);
        configuration.setOrderMap(orderMap);
        configurationRepository.save(configuration);
    }

    @Transactional
    public void addGroup(Group group) {
        List<UUID> userIdList = group.getMembers().stream()
                .map(Member::getUserId)
                .toList();
        List<Configuration> configurationList = configurationRepository.findAllByUserIdIn(userIdList);

        List<UUID> existingUserIdList = configurationList.stream()
                .map(Configuration::getUserId)
                .toList();
        userIdList.stream().filter(userId -> !existingUserIdList.contains(userId))
                .forEach(userId -> configurationList.add(new Configuration(userId)));
        configurationList.forEach(c -> addGroupToConfiguration(c, group.getId()));

        configurationRepository.saveAll(configurationList);
    }

    @Transactional
    public void deleteGroup(Group group) {
        List<UUID> userIdList = group.getMembers().stream()
                .map(Member::getUserId)
                .toList();
        List<Configuration> configurationList = configurationRepository.findAllByUserIdIn(userIdList);
        configurationList.forEach(c -> removeGroupFromConfiguration(c, group.getId()));
        configurationRepository.saveAll(configurationList);
    }


    private void addGroupToConfiguration(Configuration configuration, UUID groupId) {
        Map<UUID, Integer> orderMap = configuration.getOrderMap();
        orderMap.put(groupId, orderMap.size());
    }

    private void removeGroupFromConfiguration(Configuration configuration, UUID groupId) {
        Map<UUID, Integer> orderMap = configuration.getOrderMap();
        Integer groupIndex = orderMap.get(groupId);
        if (groupIndex != null) {
            orderMap.remove(groupId);
            orderMap.forEach((key, value) -> {
                if (value > groupIndex) {
                    orderMap.put(key, value - 1);
                }
            });
        }
    }

    private Map<UUID, Integer> buildOrderMap(List<Group> groupList,
                                             List<UUID> orderList) {
        List<UUID> groupIdList = groupList.stream().map(Group::getId).toList();
        AtomicInteger counter = new AtomicInteger(0);
        Map<UUID, Integer> orderMap = orderList.stream()
                .collect(Collectors.toMap(o -> o, o -> counter.getAndIncrement()));
        groupIdList.stream().filter(id -> !orderMap.containsKey(id))
                .forEach(id -> orderMap.put(id, counter.getAndIncrement()));
        return orderMap;
    }

}
