package com.persoff68.fatodo.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class GroupDTO extends AbstractAuditingDTO {

    private String title;

    private String color;

    private String imageFilename;

    private List<MemberDTO> members;

}
