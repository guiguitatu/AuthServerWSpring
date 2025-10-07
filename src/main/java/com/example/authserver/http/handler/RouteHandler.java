package com.example.authserver.http.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public interface RouteHandler {
    void handle(HttpExchange exchange) throws IOException;
}
