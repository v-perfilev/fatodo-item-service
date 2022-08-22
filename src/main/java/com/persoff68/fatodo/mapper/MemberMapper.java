package com.persoff68.fatodo.mapper;

import com.persoff68.fatodo.model.Member;
import com.persoff68.fatodo.model.dto.MemberDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MemberMapper {

    MemberDTO pojoToDTO(Member member);

}
