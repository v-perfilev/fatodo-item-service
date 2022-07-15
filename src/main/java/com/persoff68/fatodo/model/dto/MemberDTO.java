package com.persoff68.fatodo.model.dto;

import com.persoff68.fatodo.model.constant.Permission;
import lombok.Data;

import java.util.UUID;

@Data
public class MemberDTO {

    private UUID userId;

    private Permission permission;

}

