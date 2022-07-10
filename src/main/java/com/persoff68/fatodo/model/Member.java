package com.persoff68.fatodo.model;


import com.persoff68.fatodo.model.constant.Permission;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "ftd_item_group_member")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(Member.MemberId.class)
@ToString(exclude = {"group"})
public class Member {

    @Id
    @ManyToOne
    private Group group;

    @Id
    private UUID userId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Permission permission;

    public static Member adminMember(Group group, UUID id) {
        return new Member(group, id, Permission.ADMIN);
    }

    public static Member readMember(Group group, UUID id) {
        return new Member(group, id, Permission.READ);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberId implements Serializable {
        private Group group;
        private UUID userId;
    }

}
