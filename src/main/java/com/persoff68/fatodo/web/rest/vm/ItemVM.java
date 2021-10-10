package com.persoff68.fatodo.web.rest.vm;

import com.persoff68.fatodo.model.DateParams;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.Reminder;
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

    private UUID groupId;

    @NotNull
    private String title;

    @NotNull
    private String type;

    @NotNull
    private String priority;

    private DateParams date;

    private String description;

    private List<Reminder> reminders;

    private List<String> tags;

}
