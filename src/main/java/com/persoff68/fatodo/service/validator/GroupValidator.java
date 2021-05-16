package com.persoff68.fatodo.service.validator;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.constant.Permission;
import com.persoff68.fatodo.service.exception.GroupInvalidException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GroupValidator {

    public void validateCreate(Group group) {
        List<Group.User> userList = group.getUsers();
        checkIfUserListIsNotEmpty(userList);
        checkIfUserListContainsOneUser(userList);
    }

    public void validateUpdate(Group group) {
        List<Group.User> userList = group.getUsers();
        checkIfUserListIsNotEmpty(userList);
        checkIfListHasAdmins(userList);
    }

    public void validateDelete(Group group) {
        // no checks yet
    }

    private void checkIfUserListIsNotEmpty(List<Group.User> userList) {
        if (userList.isEmpty()) {
            throw new GroupInvalidException("Group user list is empty");
        }
    }

    private void checkIfUserListContainsOneUser(List<Group.User> userList) {
        if (userList.size() != 1) {
            throw new GroupInvalidException("List of group users contains more than one user");
        }
    }

    private void checkIfListHasAdmins(List<Group.User> userList) {
        long adminCount = userList.stream()
                .filter(groupUser -> groupUser.getPermission().equals(Permission.ADMIN))
                .count();
        if (adminCount == 0) {
            throw new GroupInvalidException("Group has no admins");
        }
    }

}
