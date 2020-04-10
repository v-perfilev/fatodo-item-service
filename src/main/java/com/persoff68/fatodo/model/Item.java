package com.persoff68.fatodo.model;

import com.persoff68.fatodo.config.constant.AppConstants;
import com.persoff68.fatodo.model.constant.ItemStatus;
import com.persoff68.fatodo.model.dto.AbstractAuditingDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document(collection = "ftd_item")
@Data
@EqualsAndHashCode(callSuper = false)
public class Item extends AbstractAuditingDTO {
    private static final long serialVersionUID = AppConstants.SERIAL_VERSION_UID;

    private ItemStatus status;
    private String title;
    private String body;
    private Set<String> groupIds;

}
