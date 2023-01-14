package com.persoff68.fatodo.mapper;

import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.constant.ItemPriority;
import com.persoff68.fatodo.model.dto.ItemDTO;
import com.persoff68.fatodo.model.dto.ItemInfoDTO;
import com.persoff68.fatodo.model.vm.ItemVM;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemMapper {

    @Mapping(target = "priority", source = "priority", qualifiedByName = "priorityNameToValue")
    Item vmToPojo(ItemVM itemVM);

    @Mapping(target = "groupId", source = "group.id")
    @Mapping(target = "priority", source = "priority", qualifiedByName = "priorityValueToObject")
    ItemDTO pojoToDTO(Item item);

    ItemInfoDTO pojoToInfoDTO(Item item);

    @Named("priorityNameToValue")
    static int priorityNameToValue(String priorityName) {
        return ItemPriority.getValueByName(priorityName);
    }

    @Named("priorityValueToObject")
    static ItemPriority priorityValueToObject(int value) {
        return ItemPriority.getByValue(value);
    }

}
