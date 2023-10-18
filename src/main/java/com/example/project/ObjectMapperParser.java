package com.example.project;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperParser {
    private final ObjectMapper objectMapper;

    public ObjectMapperParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public MyObject parseJson(String jsonString) throws Exception {
        return objectMapper.readValue(jsonString, MyObject.class);
    }

    public MyObject throwJsonMappingException() throws JsonMappingException {
        throw new JsonMappingException("Forced JsonMappingException.");
    }

}
