package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.DateParams;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.constant.ItemStatus;
import com.persoff68.fatodo.model.constant.ItemType;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class TestItem extends Item {
    private static final String DEFAULT_VALUE = "test_value";

    @Builder
    TestItem(UUID id,
             @NotNull Group group,
             @NotNull String title,
             @NotNull ItemType type,
             @NotNull int priority,
             DateParams date,
             String description,
             @NotNull ItemStatus status,
             int remindersCount,
             boolean isArchived,
             boolean isDeleted) {
        super(group, title, type, priority, status, date, description, remindersCount, isArchived, isDeleted);
        this.setId(id);
    }

    public static TestItemBuilder defaultBuilder() {
        return TestItem.builder()
                .title(DEFAULT_VALUE)
                .type(ItemType.TASK)
                .priority(2)
                .description(DEFAULT_VALUE)
                .status(ItemStatus.CREATED)
                .remindersCount(0);
    }

    public Item toParent() {
        Item item = new Item();
        item.setId(getId());
        item.setGroup(getGroup());
        item.setTitle(getTitle());
        item.setType(getType());
        item.setPriority(getPriority());
        item.setStatus(getStatus());
        item.setDate(getDate());
        item.setDescription(getDescription());
        item.setRemindersCount(getRemindersCount());
        item.setArchived(isArchived());
        item.setDeleted(isDeleted());
        return item;
    }

}
