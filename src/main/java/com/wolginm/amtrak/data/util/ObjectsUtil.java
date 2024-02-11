package com.wolginm.amtrak.data.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.stereotype.Component;


import java.util.Map;

@Slf4j
@Component
public class ObjectsUtil {

    private final ObjectMapper objectMapper;

    public ObjectsUtil(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JsonNullableModule());
    }

    public <T> T mapToObject(final Map<String, String> mapOfElements, final Class<T> tClass) {
        return objectMapper.convertValue(mapOfElements, tClass);
    }

}
