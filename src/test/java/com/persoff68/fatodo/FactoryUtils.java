package com.persoff68.fatodo;

import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.constant.ItemStatus;
import com.persoff68.fatodo.model.dto.ItemDTO;

public class FactoryUtils {

    public static Item createItem(String prefix, String groupId, ItemStatus status) {
        Item item = new Item();
        item.setTitle("test_title_" + prefix);
        item.setDescription("test_body_" + prefix);
        item.setGroupId(groupId);
        item.setStatus(status);
        return item;
    }

    public static ItemDTO createItemDTO(String prefix, String groupId, ItemStatus status) {
        ItemDTO dto = new ItemDTO();
        dto.setTitle("test_title_" + prefix);
        dto.setDescription("test_body_" + prefix);
        dto.setGroupId(groupId);
        dto.setStatus(status);
        return dto;
    }
}
