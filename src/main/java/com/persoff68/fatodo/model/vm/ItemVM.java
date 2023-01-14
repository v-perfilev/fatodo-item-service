package com.persoff68.fatodo.model.vm;

import com.persoff68.fatodo.model.Reminder;
import com.persoff68.fatodo.web.rest.validator.RemindersConstraint;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
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
    @Min(1)
    @Max(3)
    private int priority;

    private String description;

    @RemindersConstraint
    private List<Reminder> reminders;

    private boolean done;

    private boolean deleteReminders;

}
