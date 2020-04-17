package com.persoff68.fatodo.model;

import com.persoff68.fatodo.config.constant.AppConstants;
import com.persoff68.fatodo.model.constant.ItemStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

@Document(collection = "ftd_item")
@Data
@EqualsAndHashCode(callSuper = false)
public class Item extends AbstractAuditingModel {
    private static final long serialVersionUID = AppConstants.SERIAL_VERSION_UID;

    private String title;
    @NotNull
    private String body;
    @NotNull
    private ItemStatus status = ItemStatus.ACTIVE;
    @NotNull
    private String groupId;

}
