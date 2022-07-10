package com.persoff68.fatodo.model.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.persoff68.fatodo.model.DateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.persistence.AttributeConverter;
import java.io.IOException;

@Component
@Slf4j
@RequiredArgsConstructor
public class DataParamsConverter implements AttributeConverter<DateParams, String> {

    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(DateParams dateParams) {
        String s = null;
        try {
            s = objectMapper.writeValueAsString(dateParams);
        } catch (JsonProcessingException e) {
            log.error("Map converter error: ", e);
        }
        return s;
    }

    @Override
    public DateParams convertToEntityAttribute(String s) {
        DateParams dateParams = null;
        try {
            dateParams = objectMapper.readValue(s, DateParams.class);
        } catch (IOException e) {
            log.error("Map converter error: ", e);
        }
        return dateParams;
    }

}
