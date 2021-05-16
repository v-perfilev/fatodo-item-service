package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.constant.Permission;
import lombok.Builder;

import java.util.UUID;

public class TestGroupUser extends Group.User {

    @Builder
    TestGroupUser(UUID id, Permission permission) {
        super(id, permission);
    }

    public static TestGroupUserBuilder defaultBuilder() {
        return TestGroupUser.builder()
                .id(UUID.randomUUID())
                .permission(Permission.READ);
    }

}
