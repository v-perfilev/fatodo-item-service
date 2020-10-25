package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.constant.ItemPriority;
import com.persoff68.fatodo.model.constant.ItemStatus;
import com.persoff68.fatodo.model.constant.ItemType;
import com.persoff68.fatodo.model.dto.ItemDTO;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

public class TestItemDTO extends ItemDTO {
    private static final String DEFAULT_VALUE = "test_value";

    @Builder
    public TestItemDTO(UUID id, String title, ItemType type, ItemPriority priority, Item.DateParams date, String description, List<Item.Reminder> reminders, List<String> tags, ItemStatus status, UUID groupId) {
        super(title, type, priority, date, description, reminders, tags, status, groupId);
        this.setId(id);
    }

    public static TestItemDTOBuilder defaultBuilder() {
        return TestItemDTO.builder()
                .id(UUID.randomUUID())
                .title(DEFAULT_VALUE)
                .type(ItemType.TASK)
                .priority(ItemPriority.NORMAL)
                .description(DEFAULT_VALUE)
                .status(ItemStatus.ACTIVE)
                .groupId(UUID.randomUUID());
    }

}
