package com.persoff68.fatodo.model.dto;

import com.persoff68.fatodo.config.constant.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class GroupPermissionDTO extends AbstractDTO {
    private static final long serialVersionUID = AppConstants.SERIAL_VERSION_UID;

    Set<String> groupIds;
    String userId;

}
