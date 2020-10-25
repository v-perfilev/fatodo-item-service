package com.persoff68.fatodo.model;

import com.persoff68.fatodo.model.constant.ItemPriority;
import com.persoff68.fatodo.model.constant.ItemStatus;
import com.persoff68.fatodo.model.constant.ItemType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Document(collection = "ftd_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Item extends AbstractAuditingModel {

    @NotNull
    private String title;

    @NotNull
    private ItemType type;

    @NotNull
    private ItemPriority priority;

    private DateParams date;

    private String description;

    private List<Reminder> reminders;

    private List<String> tags;

    @NotNull
    private ItemStatus status;

    private UUID groupId;

    @Data
    @NoArgsConstructor
    public static class DateParams {
        private int time;
        private int date;
        private int month;
        private int year;
    }

    @Data
    @NoArgsConstructor
    public static class Reminder {
        private UUID id = UUID.randomUUID();
        private String periodicity;
        private DateParams date;
        private List<Integer> weekDays;
        private List<Integer> monthDays;
    }

}
