package com.example.authserver.http;

import com.example.authserver.http.handler.HandlerFactory;
import com.example.authserver.http.handler.RouteHandler;
import com.example.authserver.http.handler.StaticResponseHandler;
import com.example.authserver.repository.ProductRepository;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

public class ProductHttpHandler implements HttpHandler {

    private final HandlerFactory handlerFactory;
    private final RouteHandler notFoundHandler;

    public ProductHttpHandler(ProductRepository repository) {
        ResponseWriter responseWriter = ResponseWriter.INSTANCE;
        this.handlerFactory = new HandlerFactory(repository, responseWriter);
        this.notFoundHandler = new StaticResponseHandler(404, Map.of("error", "Recurso não encontrado"), responseWriter);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        RouteHandler handler = handlerFactory.create(exchange).orElse(notFoundHandler);
        handler.handle(exchange);
    }
}
