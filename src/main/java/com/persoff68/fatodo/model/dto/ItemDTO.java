package com.persoff68.fatodo.model.dto;

import com.persoff68.fatodo.config.constant.AppConstants;
import com.persoff68.fatodo.model.constant.ItemStatus;

public class ItemDTO extends AbstractAuditingDTO {
    private static final long serialVersionUID = AppConstants.SERIAL_VERSION_UID;

    private String title;
    private String body;
    private ItemStatus status;
    private String groupId;
}
