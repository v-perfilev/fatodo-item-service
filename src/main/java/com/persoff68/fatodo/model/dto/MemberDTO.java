package com.persoff68.fatodo.model.dto;

import com.persoff68.fatodo.model.constant.Permission;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class MemberDTO implements Serializable {
    private UUID userId;
    private Permission permission;
}

