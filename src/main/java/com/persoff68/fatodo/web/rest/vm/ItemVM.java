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

    private Date date;

    private String description;

    private List<Reminder> reminders;

    private List<String> tags;

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
        private String id;
        private String periodicity;
        private Item.Date date;
        private List<Integer> weekDays;
        private List<Integer> monthDays;
    }

}
