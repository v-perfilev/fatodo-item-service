package com.persoff68.fatodo.model;

import com.persoff68.fatodo.model.constant.ItemPriority;
import com.persoff68.fatodo.model.constant.ItemStatus;
import com.persoff68.fatodo.model.constant.ItemType;
import com.persoff68.fatodo.model.converter.DataParamsConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "ftd_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"group"})
public class Item extends AbstractAuditingModel {

    @ManyToOne
    private Group group;

    @NotNull
    private String title;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ItemType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ItemPriority priority;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ItemStatus status;

    @Convert(converter = DataParamsConverter.class)
    private DateParams date;

    private String description;

    private boolean isArchived;

    private boolean isDeleted;

}
