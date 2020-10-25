package com.persoff68.fatodo.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractAuditingDTO extends AbstractDTO {

    protected UUID createdBy;
    protected Instant createdAt;
    protected UUID lastModifiedBy;
    protected Instant lastModifiedAt;

}
