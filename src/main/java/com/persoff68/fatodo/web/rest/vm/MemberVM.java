package com.persoff68.fatodo.web.rest.vm;

import com.persoff68.fatodo.model.constant.Permission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberVM {
    @NotNull
    private UUID id;
    @NotNull
    private Permission permission;
}

