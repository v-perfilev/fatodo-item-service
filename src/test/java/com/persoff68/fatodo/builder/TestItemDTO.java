package com.persoff68.fatodo.builder;

import com.persoff68.fatodo.model.DateParams;
import com.persoff68.fatodo.model.constant.ItemPriority;
import com.persoff68.fatodo.model.constant.ItemStatus;
import com.persoff68.fatodo.model.constant.ItemType;
import com.persoff68.fatodo.model.dto.ItemDTO;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

public class TestItemDTO extends ItemDTO {
    private static final String DEFAULT_VALUE = "test_value";

    @Builder
    public TestItemDTO(UUID id,
                       UUID groupId,
                       String title,
                       ItemType type,
                       ItemPriority priority,
                       DateParams date,
                       String description,
                       List<String> tags,
                       ItemStatus status) {
        super(groupId, title, type, priority, date, description, tags, status);
        this.setId(id);
    }

    public static TestItemDTOBuilder defaultBuilder() {
        return TestItemDTO.builder()
                .id(UUID.randomUUID())
                .groupId(UUID.randomUUID())
                .title(DEFAULT_VALUE)
                .type(ItemType.TASK)
                .priority(ItemPriority.NORMAL)
                .description(DEFAULT_VALUE)
                .status(ItemStatus.ACTIVE);
    }

}
