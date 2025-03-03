package com.cse681;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ParseJson {
    // thread safe reusable instance
    private static final ObjectMapper mapper = new ObjectMapper();

    // method to convert JSON object to Java object
    public static JsonNode parse(String jsonResponse) throws JsonProcessingException {
        return mapper.readTree(jsonResponse);
    }
}
