package com.persoff68.fatodo.model;


import com.persoff68.fatodo.model.constant.Permission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    private UUID id;
    private Permission permission;
}
