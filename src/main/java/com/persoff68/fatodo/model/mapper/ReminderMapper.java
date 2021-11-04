package com.persoff68.fatodo.model.mapper;

import com.persoff68.fatodo.model.ReminderMessage;
import com.persoff68.fatodo.model.dto.ReminderMessageDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReminderMapper {

    ReminderMessageDTO pojoToDTO(ReminderMessage message);

}
