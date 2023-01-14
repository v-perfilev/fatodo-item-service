package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.vm.ItemStatusVM;
import lombok.Builder;

import java.util.UUID;

public class TestItemStatusVM extends ItemStatusVM {

    @Builder
    public TestItemStatusVM(UUID id, boolean done) {
        super(id, done);
    }

    public static TestItemStatusVMBuilder defaultBuilder() {
        return TestItemStatusVM.builder()
                .id(UUID.randomUUID())
                .done(true);
    }

    public ItemStatusVM toParent() {
        ItemStatusVM vm = new ItemStatusVM();
        vm.setId(getId());
        vm.setDone(isDone());
        return vm;
    }

}
