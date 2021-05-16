package com.persoff68.fatodo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "ftd_configuration")
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Configuration extends AbstractModel {

    @NotNull
    private UUID userId;

    private List<Group> groups;

    public Configuration(UUID userId) {
        this.userId = userId;
        groups = new ArrayList<>();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Group {
        private UUID id;
        private int order;

        public Group(UUID id) {
            this.id = id;
            this.order = Integer.MAX_VALUE;
        }
    }

}
