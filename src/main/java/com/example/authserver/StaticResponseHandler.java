package com.example.authserver;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class StaticResponseHandler implements RouteHandler {

    private final int statusCode;
    private final Object body;
    private final ResponseWriter responseWriter;

    public StaticResponseHandler(int statusCode, Object body, ResponseWriter responseWriter) {
        this.statusCode = statusCode;
        this.body = body;
        this.responseWriter = responseWriter;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        responseWriter.writeJson(exchange, statusCode, body);
    }
}
