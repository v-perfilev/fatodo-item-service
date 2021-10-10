package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.DateParams;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.constant.ItemPriority;
import com.persoff68.fatodo.model.constant.ItemStatus;
import com.persoff68.fatodo.model.constant.ItemType;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class TestItem extends Item {
    private static final String DEFAULT_VALUE = "test_value";

    @Builder
    TestItem(UUID id,
             @NotNull String title,
             @NotNull ItemType type,
             @NotNull ItemPriority priority,
             DateParams date,
             String description,
             List<String> tags,
             @NotNull ItemStatus status,
             UUID groupId) {
        super(groupId, title, type, priority, date, description, tags, status);
        this.setId(id);
    }

    public static TestItemBuilder defaultBuilder() {
        return TestItem.builder()
                .id(UUID.randomUUID())
                .title(DEFAULT_VALUE)
                .type(ItemType.TASK)
                .priority(ItemPriority.NORMAL)
                .description(DEFAULT_VALUE)
                .status(ItemStatus.ACTIVE)
                .groupId(UUID.randomUUID());
    }

}
