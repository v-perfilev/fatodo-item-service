package com.persoff68.fatodo.model.mapper;

import com.persoff68.fatodo.model.Item;
import com.persoff68.fatodo.model.dto.ItemDTO;
import com.persoff68.fatodo.web.rest.vm.ItemVM;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ItemMapper {

    Item vmToPojo(ItemVM itemVM);

    @Mapping(target = "groupId", source = "group.id")
    ItemDTO pojoToDTO(Item item);

}
