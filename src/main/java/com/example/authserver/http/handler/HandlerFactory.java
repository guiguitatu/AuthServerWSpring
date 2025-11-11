package com.example.authserver.http.handler;

import com.example.authserver.http.util.ResponseWriter;
import com.example.authserver.http.handler.product.CreateProductHandler;
import com.example.authserver.http.handler.product.DeleteProductHandler;
import com.example.authserver.http.handler.product.GetProductHandler;
import com.example.authserver.http.handler.product.ListProductsHandler;
import com.example.authserver.http.handler.product.UpdateProductHandler;
import com.example.authserver.repository.ProductRepository;
import com.example.authserver.validation.DefaultProductValidator;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;
import java.util.Optional;

public class HandlerFactory {

    private final RouteHandler listProductsHandler;
    private final RouteHandler createProductHandler;
    private final RouteHandler getProductHandler;
    private final RouteHandler updateProductHandler;
    private final RouteHandler deleteProductHandler;
    private final RouteHandler methodNotAllowedHandler;

    public HandlerFactory(ProductRepository repository, ResponseWriter responseWriter) {
        this.listProductsHandler = new ListProductsHandler(repository, responseWriter);
        this.createProductHandler = new CreateProductHandler(repository, responseWriter, new DefaultProductValidator());
        this.getProductHandler = new GetProductHandler(repository, responseWriter);
        this.updateProductHandler = new UpdateProductHandler(repository, responseWriter, new DefaultProductValidator());
        this.deleteProductHandler = new DeleteProductHandler(repository, responseWriter);
        this.methodNotAllowedHandler = new StaticResponseHandler(405, Map.of("error", "Método não suportado"), responseWriter);
    }

    public Optional<RouteHandler> create(HttpExchange exchange) {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("/products".equals(path)) {
            return switch (method) {
                case "GET" -> Optional.of(listProductsHandler);
                case "POST" -> Optional.of(createProductHandler);
                case "DELETE" -> Optional.of(deleteProductHandler);
                default -> Optional.of(methodNotAllowedHandler);
            };
        }

        if (path.matches("/products/\\d+")) {
            return switch (method) {
                case "GET" -> Optional.of(getProductHandler);
                case "PUT" -> Optional.of(updateProductHandler);
                case "DELETE" -> Optional.of(deleteProductHandler);
                default -> Optional.of(methodNotAllowedHandler);
            };
        }

        return Optional.empty();
    }
}
