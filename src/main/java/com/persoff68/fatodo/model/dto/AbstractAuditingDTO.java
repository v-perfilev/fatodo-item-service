package com.persoff68.fatodo.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractAuditingDTO extends AbstractDTO {

    protected String createdBy;
    protected Instant createdAt;
    protected String lastModifiedBy;
    protected Instant lastModifiedAt;

}
