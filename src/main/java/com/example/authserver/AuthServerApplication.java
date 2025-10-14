package com.example.authserver;

import com.example.authserver.http.ProductHttpHandler;
import com.example.authserver.http.RequestLoggingFilter;
import com.example.authserver.repository.ProductRepository;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthServerApplication {

    public static void main(String[] args) throws IOException {
        int port = 8080;
        DatabaseConfiguration configuration = DatabaseConfiguration.fromEnvironment();
        ProductRepository repository = new ProductRepository(
                configuration.jdbcUrl(),
                configuration.username(),
                configuration.password()
        );
        repository.initializeSampleData();

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        HttpContext context = server.createContext("/products", new ProductHttpHandler(repository));
        context.getFilters().add(new RequestLoggingFilter());

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        server.setExecutor(executor);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop(0);
            executor.shutdown();
        }));

        server.start();
        System.out.printf("Servidor iniciado na porta %d%n", port);
    }

    private record DatabaseConfiguration(String jdbcUrl, String username, String password) {

        private static DatabaseConfiguration fromEnvironment() {
            String url = envOrDefault("DB_URL", "jdbc:mysql://localhost:3306/authserver");
            String user = envOrDefault("DB_USER", "root");
            String password = envOrDefault("DB_PASSWORD", "root");
            return new DatabaseConfiguration(url, user, password);
        }

        private static String envOrDefault(String key, String defaultValue) {
            String value = System.getenv(key);
            return value != null && !value.isBlank() ? value : defaultValue;
        }
    }
}
