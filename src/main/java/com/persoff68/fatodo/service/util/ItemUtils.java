package com.persoff68.fatodo.service.util;

import com.persoff68.fatodo.model.Item;

public class ItemUtils {

    private ItemUtils() {
    }

    public static boolean areGroupIdsEquals(Item a, Item b) {
        return a.getGroupId().equals(b.getGroupId());
    }

    public static boolean areStatusesEquals(Item a, Item b) {
        return a.getStatus().equals(b.getStatus());
    }

}
