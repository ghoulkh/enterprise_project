package com.enterprise.backend.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class JsonMapper {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            return null;
        }
    }

    public static<T> T toObject(String json, TypeReference<T> tTypeToken) {
        try {
            return mapper.readValue(json, tTypeToken);
        } catch (Exception e) {
            return null;
        }
    }
}
