package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.web.rest.vm.ItemArchivedVM;
import lombok.Builder;

import java.util.UUID;

public class TestItemArchivedVM extends ItemArchivedVM {

    @Builder
    public TestItemArchivedVM(UUID id,
                              boolean archived) {
        super(id, archived);
    }

    public static TestItemArchivedVMBuilder defaultBuilder() {
        return TestItemArchivedVM.builder()
                .id(UUID.randomUUID())
                .archived(true);
    }

}
