package com.example.authserver;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public abstract class AbstractProductHandler implements RouteHandler {

    protected final ProductRepository repository;
    protected final ResponseWriter responseWriter;

    protected AbstractProductHandler(ProductRepository repository, ResponseWriter responseWriter) {
        this.repository = repository;
        this.responseWriter = responseWriter;
    }

    @Override
    public final void handle(HttpExchange exchange) throws IOException {
        doHandle(exchange);
    }

    protected int extractProductId(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        int lastSlash = path.lastIndexOf('/');
        return Integer.parseInt(path.substring(lastSlash + 1));
    }

    protected abstract void doHandle(HttpExchange exchange) throws IOException;
}
