package com.example.authserver;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;

public class CreateProductHandler extends AbstractProductHandler {

    private final ProductValidator validator;

    public CreateProductHandler(ProductRepository repository, ResponseWriter responseWriter, ProductValidator validator) {
        super(repository, responseWriter);
        this.validator = validator;
    }

    @Override
    protected void doHandle(HttpExchange exchange) throws IOException {
        Product payload = responseWriter.readJson(exchange, Product.class).orElseGet(Product::new);
        if (!validator.isValid(payload)) {
            responseWriter.writeJson(exchange, 400, Map.of("error", "Dados inválidos"));
            return;
        }
        Product created = repository.save(new Product(0, payload.getName(), payload.getDescription(), payload.getPrice()));
        responseWriter.writeJson(exchange, 201, created);
    }
}
