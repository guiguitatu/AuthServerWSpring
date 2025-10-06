package com.example.authserver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public enum ResponseWriter {
    INSTANCE;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void writeJson(HttpExchange exchange, int statusCode, Object body) throws IOException {
        byte[] responseBytes = toJsonBytes(body);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    public <T> Optional<T> readJson(HttpExchange exchange, Class<T> type) throws IOException {
        try (InputStream body = exchange.getRequestBody()) {
            if (body == null) {
                return Optional.empty();
            }
            byte[] data = body.readAllBytes();
            if (data.length == 0) {
                return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(data, type));
        }
    }

    private byte[] toJsonBytes(Object body) throws JsonProcessingException {
        if (body instanceof byte[] bytes) {
            return bytes;
        }
        if (body instanceof String s) {
            return s.getBytes(StandardCharsets.UTF_8);
        }
        return objectMapper.writeValueAsBytes(body);
    }
}
