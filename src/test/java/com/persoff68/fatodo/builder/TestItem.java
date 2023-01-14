package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class TestItem extends Item {
    private static final String DEFAULT_VALUE = "test_value";

    @Builder
    TestItem(UUID id,
             @NotNull Group group,
             @NotNull String title,
             @NotNull int priority,
             String description,
             int remindersCount,
             boolean isDone,
             boolean isArchived,
             boolean isDeleted) {
        super(group, title, priority, description, remindersCount, isDone, isArchived, isDeleted);
        this.setId(id);
    }

    public static TestItemBuilder defaultBuilder() {
        return TestItem.builder()
                .title(DEFAULT_VALUE)
                .priority(2)
                .description(DEFAULT_VALUE)
                .remindersCount(0);
    }

    public Item toParent() {
        Item item = new Item();
        item.setId(getId());
        item.setGroup(getGroup());
        item.setTitle(getTitle());
        item.setPriority(getPriority());
        item.setDescription(getDescription());
        item.setRemindersCount(getRemindersCount());
        item.setDone(isDone());
        item.setArchived(isArchived());
        item.setDeleted(isDeleted());
        return item;
    }

}
