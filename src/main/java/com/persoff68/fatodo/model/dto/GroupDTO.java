package com.persoff68.fatodo.model.dto;

import com.persoff68.fatodo.model.constant.Permission;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class GroupDTO extends AbstractAuditingDTO {

    private String title;

    private String color;

    private String imageFilename;

    private List<User> users;

    @Data
    public static class User {
        private UUID id;
        private Permission permission;
    }

}
