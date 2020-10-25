package com.persoff68.fatodo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractAuditingModel extends AbstractModel {

    @CreatedBy
    protected UUID createdBy;

    @CreatedDate
    protected Instant createdAt;

    @LastModifiedBy
    protected UUID lastModifiedBy;

    @LastModifiedDate
    protected Instant lastModifiedAt;

}

