package com.persoff68.fatodo.web.rest.vm;

import com.persoff68.fatodo.model.DateParams;
import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.web.rest.validator.DateParamsConstraint;
import com.persoff68.fatodo.web.rest.validator.RemindersConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemVM {

    private UUID id;

    @NotNull
    private UUID groupId;

    @NotNull
    private String title;

    @NotNull
    private String type;

    @NotNull
    private String priority;

    @DateParamsConstraint
    private DateParams date;

    private String description;

    @RemindersConstraint
    private List<Reminder> reminders;

    private List<String> tags;

    @NotNull
    private String status;

    private boolean archived;

    private boolean deleteReminders;

}
