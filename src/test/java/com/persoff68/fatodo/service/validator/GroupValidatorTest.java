package com.persoff68.fatodo.service.validator;

import com.persoff68.fatodo.builder.TestGroup;
import com.persoff68.fatodo.builder.TestGroupUser;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.security.jwt.JwtTokenProvider;
import com.persoff68.fatodo.service.exception.GroupInvalidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class GroupValidatorTest {
    private static final UUID USER_ID = UUID.fromString("d2eb0f4f-1736-4361-889b-b6d833dd9815");

    JwtTokenProvider jwtTokenProvider;
    GroupValidator groupValidator;

    @BeforeEach
    void setup() {
        jwtTokenProvider = mock(JwtTokenProvider.class);
        groupValidator = new GroupValidator();
    }

    @Test
    void testValidateCreate_emptyUserList() {
        Group group = TestGroup.defaultBuilder().users(Collections.emptyList()).build();
        assertThatThrownBy(() -> groupValidator.validateCreate(group)).isInstanceOf(GroupInvalidException.class);
    }

    @Test
    void testValidateCreate_moreThanOneUser() {
        Group.User groupUser1 = TestGroupUser.defaultBuilder().id(USER_ID).permission(Permission.ADMIN).build();
        Group.User groupUser2 = TestGroupUser.defaultBuilder().build();
        Group group = TestGroup.defaultBuilder().users(List.of(groupUser1, groupUser2)).build();
        assertThatThrownBy(() -> groupValidator.validateCreate(group)).isInstanceOf(GroupInvalidException.class);
    }

    @Test
    void testValidateUpdate_emptyUserList() {
        Group group = TestGroup.defaultBuilder().users(Collections.emptyList()).build();
        assertThatThrownBy(() -> groupValidator.validateUpdate(group)).isInstanceOf(GroupInvalidException.class);
    }

    @Test
    void testValidateUpdate_listHasNoAdmins() {
        Group.User groupUser = TestGroupUser.defaultBuilder().id(USER_ID).permission(Permission.READ).build();
        Group group = TestGroup.defaultBuilder().users(List.of(groupUser)).build();
        assertThatThrownBy(() -> groupValidator.validateUpdate(group)).isInstanceOf(GroupInvalidException.class);
    }

}
