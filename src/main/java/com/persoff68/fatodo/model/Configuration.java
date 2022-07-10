package com.persoff68.fatodo.model;

import com.persoff68.fatodo.model.converter.HashMapConverter;
import liquibase.repackaged.org.apache.commons.collections4.map.HashedMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "ftd_item_configuration")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Configuration {

    @Id
    private UUID userId;

    @Convert(converter = HashMapConverter.class)
    private Map<UUID, Integer> orderMap;

    public Configuration(UUID userId) {
        this.userId = userId;
        orderMap = new HashedMap<>();
    }

}
