package com.example.authserver;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public abstract class AbstractProductHandler implements RouteHandler {

    protected final ProductRepository repository;
    protected final ResponseWriter responseWriter;

    protected AbstractProductHandler(ProductRepository repository, ResponseWriter responseWriter) {
        this.repository = repository;
        this.responseWriter = responseWriter;
    }

    @Override
    public final void handle(HttpExchange exchange) throws IOException {
        try {
            doHandle(exchange);
        } catch (InvalidProductIdException exception) {
            responseWriter.writeJson(exchange, 400, Map.of("error", exception.getMessage()));
        }
    }

    protected int extractProductId(HttpExchange exchange) {
        String query = exchange.getRequestURI().getRawQuery();
        if (query != null && !query.isBlank()) {
            for (String param : query.split("&")) {
                String[] parts = param.split("=", 2);
                if (parts.length != 2) {
                    continue;
                }
                String name = decode(parts[0]);
                if (!"id".equalsIgnoreCase(name)) {
                    continue;
                }
                String value = decode(parts[1]).trim();
                if (value.isEmpty()) {
                    throw new InvalidProductIdException("Identificador de produto não informado");
                }
                return parseId(value);
            }
        }

        String path = exchange.getRequestURI().getPath();
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash > 0 && lastSlash < path.length() - 1) {
            String value = path.substring(lastSlash + 1).trim();
            if (!value.isEmpty()) {
                return parseId(value);
            }
        }

        throw new InvalidProductIdException("Identificador de produto não informado");
    }

    protected abstract void doHandle(HttpExchange exchange) throws IOException;

    private int parseId(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            throw new InvalidProductIdException("Identificador de produto inválido");
        }
    }

    private String decode(String value) {
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException exception) {
            throw new InvalidProductIdException("Identificador de produto inválido");
        }
    }

    private static class InvalidProductIdException extends RuntimeException {
        InvalidProductIdException(String message) {
            super(message);
        }
    }
}
