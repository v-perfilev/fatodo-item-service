package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.model.vm.ItemVM;
import lombok.Builder;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TestItemVM extends ItemVM {
    private static final String DEFAULT_VALUE = "test_value";

    @Builder
    public TestItemVM(UUID id,
                      UUID groupId,
                      String title,
                      int priority,
                      String description,
                      List<Reminder> reminders,
                      boolean done,
                      boolean deleteReminders) {
        super(id, groupId, title, priority, description, reminders, done, deleteReminders);
    }

    public static TestItemVMBuilder defaultBuilder() {
        Reminder reminder = TestReminder.defaultBuilder().build().toParent();
        return TestItemVM.builder()
                .id(UUID.randomUUID())
                .groupId(UUID.randomUUID())
                .title(DEFAULT_VALUE)
                .priority(2)
                .description(DEFAULT_VALUE)
                .reminders(Collections.singletonList(reminder));
    }

    public ItemVM toParent() {
        ItemVM vm = new ItemVM();
        vm.setId(getId());
        vm.setGroupId(getGroupId());
        vm.setTitle(getTitle());
        vm.setPriority(getPriority());
        vm.setDescription(getDescription());
        vm.setReminders(getReminders());
        vm.setDone(isDone());
        vm.setDeleteReminders(isDeleteReminders());
        return vm;
    }

}
