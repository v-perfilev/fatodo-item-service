package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.web.rest.vm.MemberVM;
import lombok.Builder;

import java.util.UUID;

public class TestMemberVM extends MemberVM {

    @Builder
    TestMemberVM(UUID userId, Permission permission) {
        super(userId, permission);
    }

    public static TestMemberVMBuilder defaultBuilder() {
        return TestMemberVM.builder()
                .userId(UUID.randomUUID())
                .permission(Permission.READ);
    }

    public MemberVM toParent() {
        MemberVM vm = new MemberVM();
        vm.setUserId(getUserId());
        vm.setPermission(getPermission());
        return vm;
    }

}
