package com.example.authserver;

import com.example.authserver.http.ProductHttpHandler;
import com.example.authserver.http.RequestLoggingFilter;
import com.example.authserver.repository.DatabaseDialect;
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
                configuration.password(),
                configuration.dialect()
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

    private record DatabaseConfiguration(String jdbcUrl, String username, String password, DatabaseDialect dialect) {

        private static DatabaseConfiguration fromEnvironment() {
            DatabaseDialect dialect = DatabaseDialect.fromString(envOrDefault("DB", "mysql"));
            return switch (dialect) {
                case MYSQL -> new DatabaseConfiguration(
                        envOrDefault("MYSQL_URL", envOrDefault("DB_URL", "jdbc:mysql://localhost:3306/authserver")),
                        envOrDefault("MYSQL_USER", envOrDefault("DB_USER", "root")),
                        envOrDefault("MYSQL_PASSWORD", envOrDefault("DB_PASSWORD", "root")),
                        dialect
                );
                case SQLITE -> new DatabaseConfiguration(
                        envOrDefault("SQLITE_URL", envOrDefault("DB_URL", "jdbc:sqlite:authserver.db")),
                        envOrDefault("SQLITE_USER", envOrDefault("DB_USER", "")),
                        envOrDefault("SQLITE_PASSWORD", envOrDefault("DB_PASSWORD", "")),
                        dialect
                );
            };
        }

        private static String envOrDefault(String key, String defaultValue) {
            String value = System.getenv(key);
            return value != null && !value.isBlank() ? value : defaultValue;
        }
    }
}
