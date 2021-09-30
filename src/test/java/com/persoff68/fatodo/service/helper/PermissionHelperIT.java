package com.persoff68.fatodo.service.helper;

import com.persoff68.fatodo.FatodoItemServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
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
public class PermissionHelperIT {

    private static final UUID USER_ID = UUID.fromString("d2eb0f4f-1736-4361-889b-b6d833dd9815");

    @Autowired
    PermissionHelper permissionHelper;

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanRead() {
        Member groupUser = TestMember.defaultBuilder().id(USER_ID).permission(Permission.READ).build();
        Group group = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canRead(group);
        assertThat(hasPermission).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanRead_noUser() {
        Member groupUser = TestMember.defaultBuilder().build();
        Group group = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canRead(group);
        assertThat(hasPermission).isFalse();
    }


    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanEdit() {
        Member groupUser = TestMember.defaultBuilder().id(USER_ID).permission(Permission.EDIT).build();
        Group group = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canEdit(group);
        assertThat(hasPermission).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanEdit_wrongRole() {
        Member groupUser = TestMember.defaultBuilder().id(USER_ID).permission(Permission.READ).build();
        Group group = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canEdit(group);
        assertThat(hasPermission).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanEdit_noUser() {
        Member groupUser = TestMember.defaultBuilder().build();
        Group group = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canEdit(group);
        assertThat(hasPermission).isFalse();
    }


    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdmin() {
        Member groupUser = TestMember.defaultBuilder().id(USER_ID).permission(Permission.ADMIN).build();
        Group group = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canAdmin(group);
        assertThat(hasPermission).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdmin_wrongRole() {
        Member groupUser = TestMember.defaultBuilder().id(USER_ID).permission(Permission.EDIT).build();
        Group group = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canAdmin(group);
        assertThat(hasPermission).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdmin_noUser() {
        Member groupUser = TestMember.defaultBuilder().build();
        Group group = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canAdmin(group);
        assertThat(hasPermission).isFalse();
    }

}
