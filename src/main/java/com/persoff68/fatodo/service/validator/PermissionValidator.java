package com.persoff68.fatodo.service.validator;

import com.persoff68.fatodo.client.GroupServiceClient;
import com.persoff68.fatodo.service.exception.PermissionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PermissionValidator {

    private final GroupServiceClient groupServiceClient;

    public void validateGet(List<UUID> groupIdList) {
        boolean isValid = groupServiceClient.canRead(groupIdList);
        if (!isValid) {
            throw new PermissionException("No permission for get");
        }
    }

    public void validateCreate(List<UUID> groupIdList) {
        boolean isPermitted = groupServiceClient.canAdmin(groupIdList);
        if (!isPermitted) {
            throw new PermissionException("No permission for create");
        }
    }

    public void validateUpdate(List<UUID> groupIdList) {
        boolean isPermitted = groupServiceClient.canEdit(groupIdList);
        if (!isPermitted) {
            throw new PermissionException("No permission for update");
        }
    }

    public void validateDelete(List<UUID> groupIdList) {
        boolean isPermitted = groupServiceClient.canAdmin(groupIdList);
        if (!isPermitted) {
            throw new PermissionException("No permission for delete");
        }
    }

}
