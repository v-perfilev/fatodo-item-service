package com.persoff68.fatodo.mapper;

import com.persoff68.fatodo.model.Group;
import com.persoff68.fatodo.model.dto.GroupDTO;
import com.persoff68.fatodo.model.dto.GroupInfoDTO;
import com.persoff68.fatodo.model.dto.MemberDTO;
import com.persoff68.fatodo.model.vm.GroupVM;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public abstract class GroupMapper {

    @Autowired
    private MemberMapper memberMapper;

    abstract GroupDTO defaultPojoToDTO(Group group);

    public abstract GroupInfoDTO pojoToInfoDTO(Group group);

    public abstract Group vmToPojo(GroupVM groupVM);

    public GroupDTO pojoToDTO(Group group) {
        if (group == null) {
            return null;
        }
        List<MemberDTO> memberList = group.getMembers().stream().map(memberMapper::pojoToDTO).toList();
        GroupDTO dto = defaultPojoToDTO(group);
        dto.setMembers(memberList);
        return dto;
    }

}
