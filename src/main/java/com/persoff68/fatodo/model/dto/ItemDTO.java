package com.persoff68.fatodo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ItemDTO extends AbstractAuditingDTO {

    private UUID groupId;

    private String title;

    private int priority;

    private String description;

    private int remindersCount;

    private boolean done;

    private boolean archived;

}
