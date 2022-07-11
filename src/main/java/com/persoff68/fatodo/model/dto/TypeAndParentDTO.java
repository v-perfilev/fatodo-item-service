package com.persoff68.fatodo.model.dto;

import com.persoff68.fatodo.model.constant.ElementType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypeAndParentDTO implements Serializable {
    private ElementType type;
    private UUID parentId;
}

