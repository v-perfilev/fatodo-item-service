package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.constant.ItemPriority;
import com.persoff68.fatodo.model.constant.ItemType;
import com.persoff68.fatodo.web.rest.vm.ItemVM;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class TestItemVM extends ItemVM {
    private static final String DEFAULT_VALUE = "test_value";

    @Builder
    public TestItemVM(UUID id, @NotNull String title, @NotNull String type, @NotNull String priority, Item.DateParams date, String description, List<Item.Reminder> reminders, List<String> tags, UUID groupId) {
        super(id, title, type, priority, date, description, reminders, tags, groupId);
    }

    public static TestItemVMBuilder defaultBuilder() {
        return TestItemVM.builder()
                .id(UUID.randomUUID())
                .title(DEFAULT_VALUE)
                .type(ItemType.TASK.toString())
                .priority(ItemPriority.NORMAL.toString())
                .description(DEFAULT_VALUE)
                .groupId(UUID.randomUUID());
    }

}
