package com.persoff68.fatodo.model;

import com.persoff68.fatodo.model.constant.ItemStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Document(collection = "ftd_item")
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Item extends AbstractAuditingModel {

    @NotNull
    private String title;

    @NotNull
    private String type;

    @NotNull
    private String priority;

    private Date date;

    private String description;

    private List<Reminder> reminders;

    private List<String> tags;

    @NotNull
    private ItemStatus status;

    private String groupId;

    @Data
    @NoArgsConstructor
    public static class Date {
        private int time;
        private int date;
        private int month;
        private int year;
    }

    @Data
    @NoArgsConstructor
    public static class Reminder {
        private String id = UUID.randomUUID().toString();
        private String periodicity;
        private Date date;
        private List<Integer> weekDays;
        private List<Integer> monthDays;
    }

}
