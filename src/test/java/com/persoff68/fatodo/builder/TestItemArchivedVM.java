package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.vm.ItemArchivedVM;
import lombok.Builder;

import java.util.UUID;

public class TestItemArchivedVM extends ItemArchivedVM {

    @Builder
    public TestItemArchivedVM(UUID id, boolean archived) {
        super(id, archived);
    }

    public static TestItemArchivedVMBuilder defaultBuilder() {
        return TestItemArchivedVM.builder()
                .id(UUID.randomUUID())
                .archived(true);
    }

    public ItemArchivedVM toParent() {
        ItemArchivedVM vm = new ItemArchivedVM();
        vm.setId(getId());
        vm.setArchived(isArchived());
        return vm;
    }

}
