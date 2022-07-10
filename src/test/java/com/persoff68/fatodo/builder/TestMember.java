package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.constant.Permission;
import lombok.Builder;

import java.util.UUID;

public class TestMember extends Member {

    @Builder
    TestMember(Group group, UUID userId, Permission permission) {
        super(group, userId, permission);
    }

    public static TestMemberBuilder defaultBuilder() {
        return TestMember.builder().userId(UUID.randomUUID()).permission(Permission.READ);
    }

    public Member toParent() {
        Member member = new Member();
        member.setGroup(getGroup());
        member.setUserId(getUserId());
        member.setPermission(getPermission());
        return member;
    }

}
