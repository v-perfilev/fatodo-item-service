package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.DateParams;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.constant.ItemPriority;
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
             @NotNull ItemPriority priority,
             DateParams date,
             String description,
             @NotNull ItemStatus status,
             boolean isArchived,
             boolean isDeleted) {
        super(group, title, type, priority, status, date, description, isArchived, isDeleted);
        this.setId(id);
    }

    public static TestItemBuilder defaultBuilder() {
        return TestItem.builder()
                .title(DEFAULT_VALUE)
                .type(ItemType.TASK)
                .priority(ItemPriority.NORMAL)
                .description(DEFAULT_VALUE)
                .status(ItemStatus.CREATED);
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
        item.setArchived(isArchived());
        item.setDeleted(isDeleted());
        return item;
    }

}
