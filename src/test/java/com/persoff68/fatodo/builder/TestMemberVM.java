package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.web.rest.vm.MemberVM;
import lombok.Builder;

import java.util.UUID;

public class TestMemberVM extends MemberVM {

    @Builder
    TestMemberVM(UUID id, Permission permission) {
        super(id, permission);
    }

    public static TestMemberVMBuilder defaultBuilder() {
        return TestMemberVM.builder()
                .id(UUID.randomUUID())
                .permission(Permission.READ);
    }

}
