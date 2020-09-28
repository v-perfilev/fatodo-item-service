package com.persoff68.fatodo.model.dto;

import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.constant.ItemPriority;
import com.persoff68.fatodo.model.constant.ItemStatus;
import com.persoff68.fatodo.model.constant.ItemType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class ItemDTO extends AbstractAuditingDTO {

    private String title;

    private ItemType type;

    private ItemPriority priority;

    private Date date;

    private String description;

    private List<Reminder> reminders;

    private List<String> tags;

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
        private String id;
        private String periodicity;
        private Item.Date date;
        private List<Integer> weekDays;
        private List<Integer> monthDays;
    }

}
