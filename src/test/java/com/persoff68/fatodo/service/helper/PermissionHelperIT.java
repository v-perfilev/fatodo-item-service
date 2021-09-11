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
    void testCanReadOneGroup() {
        Member groupUser = TestMember.defaultBuilder().id(USER_ID).permission(Permission.READ).build();
        Group group = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canRead(group);
        assertThat(hasPermission).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanReadOneGroup_noUser() {
        Member groupUser = TestMember.defaultBuilder().build();
        Group group = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canRead(group);
        assertThat(hasPermission).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanReadManyGroups() {
        Member groupUser = TestMember.defaultBuilder().id(USER_ID).permission(Permission.READ).build();
        Group group1 = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        Group group2 = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canRead(List.of(group1, group2));
        assertThat(hasPermission).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanReadManyGroups_noUser() {
        Member groupUser1 = TestMember.defaultBuilder().id(USER_ID).permission(Permission.READ).build();
        Group group1 = TestGroup.defaultBuilder().members(List.of(groupUser1)).build();
        Member groupUser2 = TestMember.defaultBuilder().build();
        Group group2 = TestGroup.defaultBuilder().members(List.of(groupUser2)).build();
        boolean hasPermission = permissionHelper.canRead(List.of(group1, group2));
        assertThat(hasPermission).isFalse();
    }


    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanEditOneGroup() {
        Member groupUser = TestMember.defaultBuilder().id(USER_ID).permission(Permission.EDIT).build();
        Group group = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canEdit(group);
        assertThat(hasPermission).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanEditOneGroup_wrongRole() {
        Member groupUser = TestMember.defaultBuilder().id(USER_ID).permission(Permission.READ).build();
        Group group = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canEdit(group);
        assertThat(hasPermission).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanEditOneGroup_noUser() {
        Member groupUser = TestMember.defaultBuilder().build();
        Group group = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canEdit(group);
        assertThat(hasPermission).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanEditManyGroups() {
        Member groupUser = TestMember.defaultBuilder().id(USER_ID).permission(Permission.EDIT).build();
        Group group1 = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        Group group2 = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canEdit(List.of(group1, group2));
        assertThat(hasPermission).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanEditManyGroups_wrongRole() {
        Member groupUser = TestMember.defaultBuilder().id(USER_ID).permission(Permission.READ).build();
        Group group1 = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        Group group2 = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canEdit(List.of(group1, group2));
        assertThat(hasPermission).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanEditManyGroups_noUser() {
        Member groupUser1 = TestMember.defaultBuilder().id(USER_ID).permission(Permission.EDIT).build();
        Group group1 = TestGroup.defaultBuilder().members(List.of(groupUser1)).build();
        Member groupUser2 = TestMember.defaultBuilder().build();
        Group group2 = TestGroup.defaultBuilder().members(List.of(groupUser2)).build();
        boolean hasPermission = permissionHelper.canEdit(List.of(group1, group2));
        assertThat(hasPermission).isFalse();
    }


    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminOneGroup() {
        Member groupUser = TestMember.defaultBuilder().id(USER_ID).permission(Permission.ADMIN).build();
        Group group = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canAdmin(group);
        assertThat(hasPermission).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminOneGroup_wrongRole() {
        Member groupUser = TestMember.defaultBuilder().id(USER_ID).permission(Permission.EDIT).build();
        Group group = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canAdmin(group);
        assertThat(hasPermission).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminOneGroup_noUser() {
        Member groupUser = TestMember.defaultBuilder().build();
        Group group = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canAdmin(group);
        assertThat(hasPermission).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminManyGroups() {
        Member groupUser = TestMember.defaultBuilder().id(USER_ID).permission(Permission.ADMIN).build();
        Group group1 = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        Group group2 = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canAdmin(List.of(group1, group2));
        assertThat(hasPermission).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminManyGroups_wrongRole() {
        Member groupUser = TestMember.defaultBuilder().id(USER_ID).permission(Permission.EDIT).build();
        Group group1 = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        Group group2 = TestGroup.defaultBuilder().members(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canAdmin(List.of(group1, group2));
        assertThat(hasPermission).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminManyGroups_noUser() {
        Member groupUser1 = TestMember.defaultBuilder().id(USER_ID).permission(Permission.ADMIN).build();
        Group group1 = TestGroup.defaultBuilder().members(List.of(groupUser1)).build();
        Member groupUser2 = TestMember.defaultBuilder().build();
        Group group2 = TestGroup.defaultBuilder().members(List.of(groupUser2)).build();
        boolean hasPermission = permissionHelper.canAdmin(List.of(group1, group2));
        assertThat(hasPermission).isFalse();
    }

}
