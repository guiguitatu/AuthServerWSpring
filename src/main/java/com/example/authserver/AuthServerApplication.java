package com.example.authserver;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthServerApplication {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        ProductRepository repository = new ProductRepository();
        HttpContext context = server.createContext("/products", new ProductHttpHandler(repository));
        context.getFilters().add(new RequestLoggingFilter());

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        server.setExecutor(executor);
        server.start();
        System.out.printf("Servidor iniciado na porta %d%n", port);
    }
}
