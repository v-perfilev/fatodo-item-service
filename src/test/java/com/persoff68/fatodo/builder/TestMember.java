package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.constant.Permission;
import lombok.Builder;

import java.util.UUID;

public class TestMember extends Member {

    @Builder
    TestMember(UUID id, Permission permission) {
        super(id, permission);
    }

    public static TestMemberBuilder defaultBuilder() {
        return TestMember.builder()
                .id(UUID.randomUUID())
                .permission(Permission.READ);
    }

}
