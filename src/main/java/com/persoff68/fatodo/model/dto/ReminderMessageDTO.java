package com.persoff68.fatodo.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class ReminderMessageDTO implements Serializable {
    private String message;
    private String url;
    private List<UUID> userIds;
}
