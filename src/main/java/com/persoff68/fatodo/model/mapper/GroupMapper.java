package com.persoff68.fatodo.model.mapper;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.dto.GroupDTO;
import com.persoff68.fatodo.model.dto.GroupSummaryDTO;
import com.persoff68.fatodo.web.rest.vm.GroupVM;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GroupMapper {

    GroupDTO pojoToDTO(Group group);

    GroupSummaryDTO pojoToSummaryDTO(Group group);

    Group vmToPojo(GroupVM groupVM);

}
