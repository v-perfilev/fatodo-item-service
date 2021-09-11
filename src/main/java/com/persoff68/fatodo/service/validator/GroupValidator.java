package com.persoff68.fatodo.service.validator;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.service.exception.GroupInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GroupValidator {

    public void validateCreate(Group group) {
        List<Member> memberList = group.getMembers();
        checkIfMemberListIsNotEmpty(memberList);
        checkIfMemberListContainsOneUser(memberList);
    }

    public void validateUpdate(Group group) {
        List<Member> userList = group.getMembers();
        checkIfMemberListIsNotEmpty(userList);
        checkIfMemberListHasAdmins(userList);
    }

    public void validateDelete(Group group) {
        // no checks yet
    }

    private void checkIfMemberListIsNotEmpty(List<Member> userList) {
        if (userList.isEmpty()) {
            throw new GroupInvalidException("Group member list is empty");
        }
    }

    private void checkIfMemberListContainsOneUser(List<Member> userList) {
        if (userList.size() != 1) {
            throw new GroupInvalidException("List of group members contains more than one member");
        }
    }

    private void checkIfMemberListHasAdmins(List<Member> userList) {
        long adminCount = userList.stream()
                .filter(groupUser -> groupUser.getPermission().equals(Permission.ADMIN))
                .count();
        if (adminCount == 0) {
            throw new GroupInvalidException("Group has no admins");
        }
    }

}
