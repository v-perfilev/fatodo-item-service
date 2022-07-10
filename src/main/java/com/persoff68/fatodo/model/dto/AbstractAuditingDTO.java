package com.persoff68.fatodo.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractAuditingDTO extends AbstractDTO {

    protected UUID createdBy;
    protected Date createdAt;
    protected UUID lastModifiedBy;
    protected Date lastModifiedAt;

}
