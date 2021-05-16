package com.persoff68.fatodo.service;

import com.persoff68.fatodo.model.Configuration;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.repository.ConfigurationRepository;
import com.persoff68.fatodo.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
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

    public void setOrder(List<UUID> groupIdList, UUID userId) {
        Configuration configuration = configurationRepository.findByUserId(userId)
                .orElse(new Configuration(userId));
        List<Group> groupList = groupRepository.findAllByUserId(userId);
        List<Configuration.Group> configurationGroupList = buildConfigurationGroupList(groupList, groupIdList);
        List<Configuration.Group> orderedGroupList = reorderGroupList(configurationGroupList);
        configuration.setGroups(orderedGroupList);
        configurationRepository.save(configuration);
    }

    public void addGroup(Group group) {
        List<UUID> userIdList = group.getUsers().stream().map(Group.User::getId).collect(Collectors.toList());
        List<Configuration> configurationList = configurationRepository.findAllByUserIdIn(userIdList);

        List<UUID> existingUserIdList = configurationList.stream()
                .map(Configuration::getUserId).collect(Collectors.toList());
        userIdList.stream().filter(userId -> !existingUserIdList.contains(userId))
                .forEach(userId -> configurationList.add(new Configuration(userId)));
        configurationList.forEach(c -> addGroupToConfiguration(c, group.getId()));

        dbOperation(configurationList, null);
    }

    public void removeGroup(Group group) {
        List<UUID> userIdList = group.getUsers().stream().map(Group.User::getId).collect(Collectors.toList());
        List<Configuration> configurationList = configurationRepository.findAllByUserIdIn(userIdList);

        configurationList.forEach(c -> removeGroupFromConfiguration(c, group.getId()));

        Map<Boolean, List<Configuration>> configurationListMap = configurationList.stream()
                .collect(Collectors.groupingBy(c -> c.getGroups().isEmpty()));
        dbOperation(configurationListMap.get(false), configurationListMap.get(true));
    }

    private void dbOperation(List<Configuration> toSaveList, List<Configuration> toDeleteList) {
        if (toSaveList != null && !toSaveList.isEmpty()) {
            configurationRepository.saveAll(toSaveList);
        }
        if (toDeleteList != null && !toDeleteList.isEmpty()) {
            configurationRepository.deleteAll(toDeleteList);
        }
    }

    private void addGroupToConfiguration(Configuration configuration, UUID groupId) {
        List<Configuration.Group> groupList = configuration.getGroups();
        Configuration.Group newGroup = new Configuration.Group(groupId);
        groupList.add(newGroup);
        List<Configuration.Group> orderedGroupList = reorderGroupList(groupList);
        configuration.setGroups(orderedGroupList);
    }

    private void removeGroupFromConfiguration(Configuration configuration, UUID groupId) {
        List<Configuration.Group> groupList = configuration.getGroups();
        groupList = groupList.stream()
                .filter(g -> !g.getId().equals(groupId)).collect(Collectors.toList());
        List<Configuration.Group> orderedGroupList = reorderGroupList(groupList);
        configuration.setGroups(orderedGroupList);
    }

    private List<Configuration.Group> reorderGroupList(List<Configuration.Group> groupList) {
        AtomicInteger counter = new AtomicInteger(0);
        return groupList.stream()
                .sorted(Comparator.comparingInt(Configuration.Group::getOrder))
                .peek(g -> g.setOrder(counter.getAndIncrement())).collect(Collectors.toList());
    }

    private List<Configuration.Group> buildConfigurationGroupList(List<Group> groupList,
                                                                  List<UUID> orderList) {
        List<UUID> groupIdList = groupList.stream()
                .map(Group::getId).collect(Collectors.toList());
        AtomicInteger counter = new AtomicInteger(0);
        Map<UUID, Integer> orderMap = orderList.stream()
                .collect(Collectors.toMap(o -> o, o -> counter.getAndIncrement()));
        return groupIdList.stream()
                .map(groupId -> new Configuration.Group(groupId, orderMap.getOrDefault(groupId, Integer.MAX_VALUE)))
                .collect(Collectors.toList());
    }

}
