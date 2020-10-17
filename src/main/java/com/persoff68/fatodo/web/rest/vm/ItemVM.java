package com.persoff68.fatodo.web.rest.vm;

import com.persoff68.fatodo.model.Item;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
public class ItemVM {

    private String id;

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

    private String groupId;

}
