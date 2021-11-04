package com.persoff68.fatodo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReminderMessage {
    private String message;
    private String url;
    private List<UUID> userIds;
}
