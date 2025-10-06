package com.example.authserver;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class GetProductHandler extends AbstractProductHandler {

    public GetProductHandler(ProductRepository repository, ResponseWriter responseWriter) {
        super(repository, responseWriter);
    }

    @Override
    protected void doHandle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();
        int id = Integer.parseInt(path.substring(path.lastIndexOf('/') + 1));
        Optional<Product> product = repository.findById(id);
        if (product.isPresent()) {
            responseWriter.writeJson(exchange, 200, product.get());
        } else {
            responseWriter.writeJson(exchange, 404, Map.of("error", "Produto não encontrado"));
        }
    }
}
