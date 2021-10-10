package com.persoff68.fatodo.model;

import com.persoff68.fatodo.model.constant.ItemPriority;
import com.persoff68.fatodo.model.constant.ItemStatus;
import com.persoff68.fatodo.model.constant.ItemType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Document(collection = "ftd_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Item extends AbstractAuditingModel {

    @Indexed
    private UUID groupId;

    @NotNull
    private String title;

    @NotNull
    private ItemType type;

    @NotNull
    private ItemPriority priority;

    private DateParams date;

    private String description;

    private List<String> tags;

    @NotNull
    private ItemStatus status;

}
