package com.persoff68.fatodo.service.helper;

import com.persoff68.fatodo.FatodoItemServiceApplication;
import com.persoff68.fatodo.builder.TestGroup;
import com.persoff68.fatodo.builder.TestMember;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.constant.Permission;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = FatodoItemServiceApplication.class)
class PermissionHelperIT {

    private static final UUID USER_ID = UUID.fromString("d2eb0f4f-1736-4361-889b-b6d833dd9815");

    @Autowired
    PermissionHelper permissionHelper;

    @Test
    void testCanRead() {
        Member groupUser = TestMember.defaultBuilder().userId(USER_ID).permission(Permission.READ).build().toParent();
        Group group = TestGroup.defaultBuilder().members(List.of(groupUser)).build().toParent();
        boolean hasPermission = permissionHelper.canRead(USER_ID, group);
        assertThat(hasPermission).isTrue();
    }

    @Test
    void testCanRead_noUser() {
        Member groupUser = TestMember.defaultBuilder().build().toParent();
        Group group = TestGroup.defaultBuilder().members(List.of(groupUser)).build().toParent();
        boolean hasPermission = permissionHelper.canRead(USER_ID, group);
        assertThat(hasPermission).isFalse();
    }


    @Test
    void testCanEdit() {
        Member groupUser = TestMember.defaultBuilder().userId(USER_ID).permission(Permission.EDIT).build().toParent();
        Group group = TestGroup.defaultBuilder().members(List.of(groupUser)).build().toParent();
        boolean hasPermission = permissionHelper.canEdit(USER_ID, group);
        assertThat(hasPermission).isTrue();
    }

    @Test
    void testCanEdit_wrongRole() {
        Member groupUser = TestMember.defaultBuilder().userId(USER_ID).permission(Permission.READ).build().toParent();
        Group group = TestGroup.defaultBuilder().members(List.of(groupUser)).build().toParent();
        boolean hasPermission = permissionHelper.canEdit(USER_ID, group);
        assertThat(hasPermission).isFalse();
    }

    @Test
    void testCanEdit_noUser() {
        Member groupUser = TestMember.defaultBuilder().build().toParent();
        Group group = TestGroup.defaultBuilder().members(List.of(groupUser)).build().toParent();
        boolean hasPermission = permissionHelper.canEdit(USER_ID, group);
        assertThat(hasPermission).isFalse();
    }


    @Test
    void testCanAdmin() {
        Member groupUser = TestMember.defaultBuilder().userId(USER_ID).permission(Permission.ADMIN).build().toParent();
        Group group = TestGroup.defaultBuilder().members(List.of(groupUser)).build().toParent();
        boolean hasPermission = permissionHelper.canAdmin(USER_ID, group);
        assertThat(hasPermission).isTrue();
    }

    @Test
    void testCanAdmin_wrongRole() {
        Member groupUser = TestMember.defaultBuilder().userId(USER_ID).permission(Permission.EDIT).build().toParent();
        Group group = TestGroup.defaultBuilder().members(List.of(groupUser)).build().toParent();
        boolean hasPermission = permissionHelper.canAdmin(USER_ID, group);
        assertThat(hasPermission).isFalse();
    }

    @Test
    void testCanAdmin_noUser() {
        Member groupUser = TestMember.defaultBuilder().build().toParent();
        Group group = TestGroup.defaultBuilder().members(List.of(groupUser)).build().toParent();
        boolean hasPermission = permissionHelper.canAdmin(USER_ID, group);
        assertThat(hasPermission).isFalse();
    }

}
