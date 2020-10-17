package com.persoff68.fatodo.model.dto;

import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.constant.ItemPriority;
import com.persoff68.fatodo.model.constant.ItemStatus;
import com.persoff68.fatodo.model.constant.ItemType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemDTO extends AbstractAuditingDTO {

    private String title;

    private ItemType type;

    private ItemPriority priority;

    private Item.DateParams date;

    private String description;

    private List<Item.Reminder> reminders;

    private List<String> tags;

    private ItemStatus status;

    private String groupId;

}
