package com.example.authserver.http.handler.product;

import com.example.authserver.domain.Product;
import com.example.authserver.http.ResponseWriter;
import com.example.authserver.repository.ProductRepository;
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
        int id = extractProductId(exchange);
        Optional<Product> product = repository.findById(id);
        if (product.isPresent()) {
            responseWriter.writeJson(exchange, 200, product.get());
        } else {
            responseWriter.writeJson(exchange, 404, Map.of("error", "Produto não encontrado"));
        }
    }
}
