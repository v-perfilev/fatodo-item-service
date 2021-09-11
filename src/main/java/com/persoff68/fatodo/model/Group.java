package com.persoff68.fatodo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.List;

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
    private List<Member> members;

    public Group(Group group) {
        this.title = group.title;
        this.color = group.color;
        this.imageFilename = group.imageFilename;

        this.members = List.copyOf(group.members);

        this.id = group.id;
        this.createdBy = group.createdBy;
        this.createdAt = group.createdAt;
        this.lastModifiedBy = group.lastModifiedBy;
        this.lastModifiedAt = group.lastModifiedAt;
    }

}
