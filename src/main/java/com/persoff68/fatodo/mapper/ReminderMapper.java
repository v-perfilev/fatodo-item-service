package com.persoff68.fatodo.mapper;

import com.persoff68.fatodo.model.ReminderInfo;
import com.persoff68.fatodo.model.dto.ReminderInfoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReminderMapper {

    ReminderInfoDTO pojoToDTO(ReminderInfo message);

}
