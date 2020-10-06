package com.persoff68.fatodo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractAuditingModel extends AbstractModel {

    @CreatedBy
    protected String createdBy;

    @CreatedDate
    protected Instant createdAt;

    @LastModifiedBy
    protected String lastModifiedBy;

    @LastModifiedDate
    protected Instant lastModifiedAt;

}

