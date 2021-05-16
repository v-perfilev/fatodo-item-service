package com.persoff68.fatodo.service.helper;

import com.persoff68.fatodo.FatodoItemServiceApplication;
import com.persoff68.fatodo.annotation.WithCustomSecurityContext;
import com.persoff68.fatodo.builder.TestGroup;
import com.persoff68.fatodo.builder.TestGroupUser;
import com.persoff68.fatodo.model.Group;
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
        Group.User groupUser = TestGroupUser.defaultBuilder().id(USER_ID).permission(Permission.READ).build();
        Group group = TestGroup.defaultBuilder().users(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canRead(group);
        assertThat(hasPermission).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanReadOneGroup_noUser() {
        Group.User groupUser = TestGroupUser.defaultBuilder().build();
        Group group = TestGroup.defaultBuilder().users(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canRead(group);
        assertThat(hasPermission).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanReadManyGroups() {
        Group.User groupUser = TestGroupUser.defaultBuilder().id(USER_ID).permission(Permission.READ).build();
        Group group1 = TestGroup.defaultBuilder().users(List.of(groupUser)).build();
        Group group2 = TestGroup.defaultBuilder().users(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canRead(List.of(group1, group2));
        assertThat(hasPermission).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanReadManyGroups_noUser() {
        Group.User groupUser1 = TestGroupUser.defaultBuilder().id(USER_ID).permission(Permission.READ).build();
        Group group1 = TestGroup.defaultBuilder().users(List.of(groupUser1)).build();
        Group.User groupUser2 = TestGroupUser.defaultBuilder().build();
        Group group2 = TestGroup.defaultBuilder().users(List.of(groupUser2)).build();
        boolean hasPermission = permissionHelper.canRead(List.of(group1, group2));
        assertThat(hasPermission).isFalse();
    }


    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanEditOneGroup() {
        Group.User groupUser = TestGroupUser.defaultBuilder().id(USER_ID).permission(Permission.EDIT).build();
        Group group = TestGroup.defaultBuilder().users(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canEdit(group);
        assertThat(hasPermission).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanEditOneGroup_wrongRole() {
        Group.User groupUser = TestGroupUser.defaultBuilder().id(USER_ID).permission(Permission.READ).build();
        Group group = TestGroup.defaultBuilder().users(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canEdit(group);
        assertThat(hasPermission).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanEditOneGroup_noUser() {
        Group.User groupUser = TestGroupUser.defaultBuilder().build();
        Group group = TestGroup.defaultBuilder().users(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canEdit(group);
        assertThat(hasPermission).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanEditManyGroups() {
        Group.User groupUser = TestGroupUser.defaultBuilder().id(USER_ID).permission(Permission.EDIT).build();
        Group group1 = TestGroup.defaultBuilder().users(List.of(groupUser)).build();
        Group group2 = TestGroup.defaultBuilder().users(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canEdit(List.of(group1, group2));
        assertThat(hasPermission).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanEditManyGroups_wrongRole() {
        Group.User groupUser = TestGroupUser.defaultBuilder().id(USER_ID).permission(Permission.READ).build();
        Group group1 = TestGroup.defaultBuilder().users(List.of(groupUser)).build();
        Group group2 = TestGroup.defaultBuilder().users(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canEdit(List.of(group1, group2));
        assertThat(hasPermission).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanEditManyGroups_noUser() {
        Group.User groupUser1 = TestGroupUser.defaultBuilder().id(USER_ID).permission(Permission.EDIT).build();
        Group group1 = TestGroup.defaultBuilder().users(List.of(groupUser1)).build();
        Group.User groupUser2 = TestGroupUser.defaultBuilder().build();
        Group group2 = TestGroup.defaultBuilder().users(List.of(groupUser2)).build();
        boolean hasPermission = permissionHelper.canEdit(List.of(group1, group2));
        assertThat(hasPermission).isFalse();
    }


    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminOneGroup() {
        Group.User groupUser = TestGroupUser.defaultBuilder().id(USER_ID).permission(Permission.ADMIN).build();
        Group group = TestGroup.defaultBuilder().users(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canAdmin(group);
        assertThat(hasPermission).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminOneGroup_wrongRole() {
        Group.User groupUser = TestGroupUser.defaultBuilder().id(USER_ID).permission(Permission.EDIT).build();
        Group group = TestGroup.defaultBuilder().users(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canAdmin(group);
        assertThat(hasPermission).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminOneGroup_noUser() {
        Group.User groupUser = TestGroupUser.defaultBuilder().build();
        Group group = TestGroup.defaultBuilder().users(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canAdmin(group);
        assertThat(hasPermission).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminManyGroups() {
        Group.User groupUser = TestGroupUser.defaultBuilder().id(USER_ID).permission(Permission.ADMIN).build();
        Group group1 = TestGroup.defaultBuilder().users(List.of(groupUser)).build();
        Group group2 = TestGroup.defaultBuilder().users(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canAdmin(List.of(group1, group2));
        assertThat(hasPermission).isTrue();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminManyGroups_wrongRole() {
        Group.User groupUser = TestGroupUser.defaultBuilder().id(USER_ID).permission(Permission.EDIT).build();
        Group group1 = TestGroup.defaultBuilder().users(List.of(groupUser)).build();
        Group group2 = TestGroup.defaultBuilder().users(List.of(groupUser)).build();
        boolean hasPermission = permissionHelper.canAdmin(List.of(group1, group2));
        assertThat(hasPermission).isFalse();
    }

    @Test
    @WithCustomSecurityContext(id = "d2eb0f4f-1736-4361-889b-b6d833dd9815")
    void testCanAdminManyGroups_noUser() {
        Group.User groupUser1 = TestGroupUser.defaultBuilder().id(USER_ID).permission(Permission.ADMIN).build();
        Group group1 = TestGroup.defaultBuilder().users(List.of(groupUser1)).build();
        Group.User groupUser2 = TestGroupUser.defaultBuilder().build();
        Group group2 = TestGroup.defaultBuilder().users(List.of(groupUser2)).build();
        boolean hasPermission = permissionHelper.canAdmin(List.of(group1, group2));
        assertThat(hasPermission).isFalse();
    }

}
