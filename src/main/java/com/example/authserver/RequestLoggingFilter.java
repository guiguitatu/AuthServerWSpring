package com.example.authserver;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.time.Instant;

public class RequestLoggingFilter extends Filter {
    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        Instant start = Instant.now();
        try {
            chain.doFilter(exchange);
        } finally {
            long duration = Instant.now().toEpochMilli() - start.toEpochMilli();
            System.out.printf("%s %s -> %dms%n", exchange.getRequestMethod(), exchange.getRequestURI(), duration);
        }
    }

    @Override
    public String description() {
        return "Registra logs simples para cada requisição";
    }
}
