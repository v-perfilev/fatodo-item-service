package com.persoff68.fatodo.model.vm;

import com.persoff68.fatodo.web.rest.validator.ItemStatusConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemStatusVM {

    private UUID id;

    @ItemStatusConstraint
    private String status;

}
