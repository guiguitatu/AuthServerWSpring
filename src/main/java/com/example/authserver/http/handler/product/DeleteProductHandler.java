package com.example.authserver.http.handler.product;

import com.example.authserver.http.ResponseWriter;
import com.example.authserver.repository.ProductRepository;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.Map;

public class DeleteProductHandler extends AbstractProductHandler {

    public DeleteProductHandler(ProductRepository repository, ResponseWriter responseWriter) {
        super(repository, responseWriter);
    }

    @Override
    protected void doHandle(HttpExchange exchange) throws IOException {
        int id = extractProductId(exchange);
        boolean removed = repository.deleteById(id);
        if (!removed) {
            responseWriter.writeJson(exchange, 404, Map.of("error", "Produto não encontrado"));
            return;
        }

        exchange.getResponseHeaders().remove("Content-Type");
        exchange.sendResponseHeaders(204, -1);
        exchange.getResponseBody().close();
    }
}
