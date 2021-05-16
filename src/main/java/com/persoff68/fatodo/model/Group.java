package com.persoff68.fatodo.model;

import com.persoff68.fatodo.model.constant.Permission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "ftd_group")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Group extends AbstractAuditingModel {

    @NotNull
    private String title;

    @NotNull
    private String color;

    private String imageFilename;

    @NotNull
    private List<User> users;

    public Group(Group group) {
        this.title = group.title;
        this.color = group.color;
        this.imageFilename = group.imageFilename;

        this.users = new ArrayList<>(group.users);

        this.id = group.id;
        this.createdBy = group.createdBy;
        this.createdAt = group.createdAt;
        this.lastModifiedBy = group.lastModifiedBy;
        this.lastModifiedAt = group.lastModifiedAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        private UUID id;
        private Permission permission;
    }

}
