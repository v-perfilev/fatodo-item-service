package com.persoff68.fatodo.model.vm;

import com.persoff68.fatodo.config.constant.AppConstants;
import com.persoff68.fatodo.web.rest.validator.ItemStatusConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemStatusVM implements Serializable {
    @Serial
    private static final long serialVersionUID = AppConstants.SERIAL_VERSION_UID;

    private UUID id;
    @ItemStatusConstraint
    private String status;

}
