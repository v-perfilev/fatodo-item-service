package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.DateParams;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.constant.ItemPriority;
import com.persoff68.fatodo.model.constant.ItemType;
import com.persoff68.fatodo.web.rest.vm.ItemVM;
import lombok.Builder;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TestItemVM extends ItemVM {
    private static final String DEFAULT_VALUE = "test_value";

    @Builder
    public TestItemVM(UUID id,
                      UUID groupId,
                      @NotNull String title,
                      @NotNull String type,
                      @NotNull String priority,
                      DateParams date,
                      String description,
                      List<Reminder> reminders,
                      List<String> tags,
                      boolean deleteReminders) {
        super(id, groupId, title, type, priority, date, description, reminders, tags, deleteReminders);
    }

    public static TestItemVMBuilder defaultBuilder() {
        Reminder reminder = TestReminder.defaultBuilder().build();
        return TestItemVM.builder()
                .id(UUID.randomUUID())
                .groupId(UUID.randomUUID())
                .title(DEFAULT_VALUE)
                .type(ItemType.TASK.toString())
                .priority(ItemPriority.NORMAL.toString())
                .description(DEFAULT_VALUE)
                .reminders(Collections.singletonList(reminder));
    }

}
