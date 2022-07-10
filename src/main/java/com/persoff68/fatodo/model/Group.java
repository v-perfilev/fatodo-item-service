package com.persoff68.fatodo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "ftd_item_group")
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

    private boolean isDeleted;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group", orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Member> members;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group", orphanRemoval = true)
    private List<Item> items;


}
