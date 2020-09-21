package com.persoff68.fatodo.web.rest.vm;

import com.persoff68.fatodo.model.Item;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ItemVM {

    private String id;

    private String title;

    private String type;

    private String priority;

    private Date date;

    private String description;

    private Reminder[] reminders;

    private String[] tags;

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
