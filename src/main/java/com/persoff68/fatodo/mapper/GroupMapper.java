package com.persoff68.fatodo.mapper;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.dto.GroupDTO;
import com.persoff68.fatodo.model.dto.GroupInfoDTO;
import com.persoff68.fatodo.model.vm.GroupVM;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GroupMapper {

    GroupDTO pojoToDTO(Group group);

    GroupInfoDTO pojoToInfoDTO(Group group);

    Group vmToPojo(GroupVM groupVM);

}
