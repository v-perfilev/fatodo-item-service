package com.persoff68.fatodo.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.MapType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class HashMapConverter implements AttributeConverter<Map<UUID, Integer>, String> {

    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(Map<UUID, Integer> map) {
        String s = "";
        try {
            s = objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            log.error("Map converter error: ", e);
        }
        return s;
    }

    @Override
    public Map<UUID, Integer> convertToEntityAttribute(String s) {
        Map<UUID, Integer> map = new HashMap<>();
        try {
            MapType mapType = objectMapper.getTypeFactory().constructMapType(HashMap.class, UUID.class, Integer.class);
            map = objectMapper.readValue(s, mapType);
        } catch (IOException e) {
            log.error("Map converter error: ", e);
        }
        return map;
    }

}
