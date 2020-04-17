package com.persoff68.fatodo.service.util;

import com.persoff68.fatodo.FactoryUtils;
import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.constant.ItemStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemUtilsTest {

    @Test
    void testAreGroupIdsEquals() {
        Item a = FactoryUtils.createItem("1", "test_group_id", ItemStatus.ACTIVE);
        Item b = FactoryUtils.createItem("2", "test_group_id", ItemStatus.ACTIVE);
        boolean result = ItemUtils.areGroupIdsEquals(a, b);
        assertThat(result).isTrue();
    }

    @Test
    void testAreStatusesEquals() {
        Item a = FactoryUtils.createItem("1", "test_group_id", ItemStatus.ACTIVE);
        Item b = FactoryUtils.createItem("2", "test_group_id", ItemStatus.ACTIVE);
        boolean result = ItemUtils.areStatusesEquals(a, b);
        assertThat(result).isTrue();
    }

}
