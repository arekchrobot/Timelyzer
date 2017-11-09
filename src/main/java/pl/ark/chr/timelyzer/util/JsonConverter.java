package pl.ark.chr.timelyzer.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonConverter {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static <T extends Object> T toPojo(String json, Class<T> clazz) throws IOException {
        return objectMapper.readValue(json, clazz);
    }

    public static String toJson(Object pojo) throws JsonProcessingException {
        return objectMapper.writeValueAsString(pojo);
    }
}
