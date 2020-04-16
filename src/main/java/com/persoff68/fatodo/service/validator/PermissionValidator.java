package com.persoff68.fatodo.service.validator;

import com.persoff68.fatodo.client.GroupServiceClient;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.service.exception.PermissionException;
import com.persoff68.fatodo.service.util.ItemUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PermissionValidator {

    private final GroupServiceClient groupServiceClient;

    public void validateGet(List<String> groupIdList) {
        boolean isValid = groupServiceClient.canRead(groupIdList);
        if (!isValid) {
            throw new PermissionException("No permission for get");
        }
    }

    public void validateCreate(Item item) {
        List<String> groupIds = List.of(item.getGroupId());
        boolean isValid = groupServiceClient.canAdmin(groupIds);
        if (!isValid) {
            throw new PermissionException("No permission for create");
        }
    }

    public void validateUpdate(Item newItem, Item oldItem) {
        boolean isValid;
        if (!ItemUtils.areGroupIdsEquals(newItem, oldItem)) {
            List<String> groupIds = List.of(newItem.getGroupId(), oldItem.getGroupId());
            isValid = groupServiceClient.canAdmin(groupIds);
        } else if (!ItemUtils.areStatusesEquals(newItem, oldItem)) {
            List<String> groupIds = List.of(newItem.getGroupId());
            isValid = groupServiceClient.canAdmin(groupIds);
        } else {
            List<String> groupIds = List.of(newItem.getGroupId());
            isValid = groupServiceClient.canEdit(groupIds);
        }
        if (!isValid) {
            throw new PermissionException("No permission for update");
        }
    }

    public void validateDelete(Item item) {
        List<String> groupIds = List.of(item.getGroupId());
        boolean isValid = groupServiceClient.canAdmin(groupIds);
        if (!isValid) {
            throw new PermissionException("No permission for delete");
        }
    }

}
