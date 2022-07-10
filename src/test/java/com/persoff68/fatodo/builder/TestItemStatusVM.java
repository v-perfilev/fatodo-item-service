package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.web.rest.vm.ItemStatusVM;
import lombok.Builder;

import java.util.UUID;

public class TestItemStatusVM extends ItemStatusVM {

    @Builder
    public TestItemStatusVM(UUID id, String status) {
        super(id, status);
    }

    public static TestItemStatusVMBuilder defaultBuilder() {
        return TestItemStatusVM.builder()
                .id(UUID.randomUUID())
                .status("CREATED");
    }

    public ItemStatusVM toParent() {
        ItemStatusVM vm = new ItemStatusVM();
        vm.setId(getId());
        vm.setStatus(getStatus());
        return vm;
    }

}
