package com.persoff68.fatodo.model.dto;

import com.persoff68.fatodo.config.constant.AppConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class GroupDTO extends AbstractAuditingDTO {
    @Serial
    private static final long serialVersionUID = AppConstants.SERIAL_VERSION_UID;

    private String title;

    private String color;

    private String imageFilename;

    private List<MemberDTO> members;

}
