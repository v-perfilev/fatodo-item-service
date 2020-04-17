package com.persoff68.fatodo.model.mapper;

import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.dto.ItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemMapper {

    Item itemDTOToItem(ItemDTO itemDTO);

    ItemDTO itemToItemDTO(Item item);

}
