package com.persoff68.fatodo.mapper;

import com.persoff68.fatodo.model.ReminderMailInfo;
import com.persoff68.fatodo.model.dto.ReminderMailInfoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReminderMapper {

    ReminderMailInfoDTO pojoToDTO(ReminderMailInfo message);

}
