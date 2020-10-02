package com.persoff68.fatodo;

import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.constant.ItemPriority;
import com.persoff68.fatodo.model.constant.ItemStatus;
import com.persoff68.fatodo.model.constant.ItemType;
import com.persoff68.fatodo.model.dto.ItemDTO;

public class FactoryUtils {

    public static Item createItem(String prefix, String groupId, ItemType type, ItemStatus status) {
        Item item = new Item();
        item.setTitle("test_title_" + prefix);
        item.setType(type);
        item.setPriority(ItemPriority.NORMAL);
        item.setDescription("test_body_" + prefix);
        item.setStatus(status);
        item.setGroupId(groupId);
        return item;
    }

    public static ItemDTO createItemDTO(String prefix, String groupId, ItemType type, ItemStatus status) {
        ItemDTO dto = new ItemDTO();
        dto.setTitle("test_title_" + prefix);
        dto.setType(type);
        dto.setPriority(ItemPriority.NORMAL);
        dto.setDescription("test_body_" + prefix);
        dto.setStatus(status);
        dto.setGroupId(groupId);
        return dto;
    }
}
