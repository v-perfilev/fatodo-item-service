package com.persoff68.fatodo.web.rest.vm;

import com.persoff68.fatodo.model.Item;
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
    private String title;

    @NotNull
    private String type;

    @NotNull
    private String priority;

    private Item.DateParams date;

    private String description;

    private List<Item.Reminder> reminders;

    private List<String> tags;

    private UUID groupId;

}
