package com.example.authserver.http.handler.product;

import com.example.authserver.http.util.ResponseWriter;
import com.example.authserver.repository.ProductRepository;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class ListProductsHandler extends AbstractProductHandler {

    public ListProductsHandler(ProductRepository repository, ResponseWriter responseWriter) {
        super(repository, responseWriter);
    }

    @Override
    protected void doHandle(HttpExchange exchange) throws IOException {
        responseWriter.writeJson(exchange, 200, repository.findAll());
    }
}
