package com.persoff68.fatodo.service.validator;

import com.persoff68.fatodo.builder.TestGroup;
import com.persoff68.fatodo.builder.TestMember;
import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Member;
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
class GroupValidatorTest {
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
        Group group = TestGroup.defaultBuilder().members(Collections.emptyList()).build();
        assertThatThrownBy(() -> groupValidator.validateCreate(group)).isInstanceOf(GroupInvalidException.class);
    }

    @Test
    void testValidateCreate_moreThanOneUser() {
        Member member1 = TestMember.defaultBuilder().id(USER_ID).permission(Permission.ADMIN).build();
        Member member2 = TestMember.defaultBuilder().build();
        Group group = TestGroup.defaultBuilder().members(List.of(member1, member2)).build();
        assertThatThrownBy(() -> groupValidator.validateCreate(group)).isInstanceOf(GroupInvalidException.class);
    }

    @Test
    void testValidateUpdate_emptyUserList() {
        Group group = TestGroup.defaultBuilder().members(Collections.emptyList()).build();
        assertThatThrownBy(() -> groupValidator.validateUpdate(group)).isInstanceOf(GroupInvalidException.class);
    }

    @Test
    void testValidateUpdate_listHasNoAdmins() {
        Member member = TestMember.defaultBuilder().id(USER_ID).permission(Permission.READ).build();
        Group group = TestGroup.defaultBuilder().members(List.of(member)).build();
        assertThatThrownBy(() -> groupValidator.validateUpdate(group)).isInstanceOf(GroupInvalidException.class);
    }

}
