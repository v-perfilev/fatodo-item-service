package com.persoff68.fatodo.model.dto;

import com.persoff68.fatodo.config.constant.AppConstants;
import com.persoff68.fatodo.model.constant.ItemStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false)
public class ItemDTO extends AbstractAuditingDTO {
    private static final long serialVersionUID = AppConstants.SERIAL_VERSION_UID;

    private String title;
    @NotNull
    private String body;
    @NotNull
    private ItemStatus status;
    @NotNull
    private String groupId;
}
