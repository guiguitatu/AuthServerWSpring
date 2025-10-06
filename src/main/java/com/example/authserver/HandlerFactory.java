package com.example.authserver;

import com.sun.net.httpserver.HttpExchange;

import java.util.Map;
import java.util.Optional;

public class HandlerFactory {

    private final RouteHandler listProductsHandler;
    private final RouteHandler createProductHandler;
    private final RouteHandler getProductHandler;
    private final RouteHandler methodNotAllowedHandler;

    public HandlerFactory(ProductRepository repository, ResponseWriter responseWriter) {
        this.listProductsHandler = new ListProductsHandler(repository, responseWriter);
        this.createProductHandler = new CreateProductHandler(repository, responseWriter, new DefaultProductValidator());
        this.getProductHandler = new GetProductHandler(repository, responseWriter);
        this.methodNotAllowedHandler = new StaticResponseHandler(405, Map.of("error", "Método não suportado"), responseWriter);
    }

    public Optional<RouteHandler> create(HttpExchange exchange) {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("/products".equals(path)) {
            return switch (method) {
                case "GET" -> Optional.of(listProductsHandler);
                case "POST" -> Optional.of(createProductHandler);
                default -> Optional.of(methodNotAllowedHandler);
            };
        }

        if (path.matches("/products/\\d+")) {
            if ("GET".equals(method)) {
                return Optional.of(getProductHandler);
            }
            return Optional.of(methodNotAllowedHandler);
        }

        return Optional.empty();
    }
}
