package com.example.authserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductHttpHandlerIntegrationTest {

    private HttpServer server;
    private HttpClient client;
    private ObjectMapper objectMapper;
    private int port;
    private EntityManagerFactory entityManagerFactory;

    @BeforeEach
    void setUp() throws IOException {
        entityManagerFactory = Persistence.createEntityManagerFactory("authServerPU", Map.of(
                "jakarta.persistence.jdbc.url", "jdbc:h2:mem:product-http-handler-test-" + UUID.randomUUID() + ";MODE=LEGACY"
        ));
        ProductRepository repository = new ProductRepository(entityManagerFactory);
        server = HttpServer.create(new InetSocketAddress(0), 0);
        HttpContext context = server.createContext("/products", new ProductHttpHandler(repository));
        context.getFilters().clear();
        server.start();
        port = server.getAddress().getPort();
        client = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
        entityManagerFactory.close();
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        Product created = createProduct("Tablet", "Tablet 10", "1500.00");

        String updatePayload = "{" +
                "\"name\":\"Tablet Pro\"," +
                "\"description\":\"Tablet atualizado\"," +
                "\"price\":1999.90" +
                "}";

        HttpRequest request = HttpRequest.newBuilder(uri("/products/" + created.getId()))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(updatePayload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Product updated = objectMapper.readValue(response.body(), Product.class);
        assertEquals(created.getId(), updated.getId());
        assertEquals("Tablet Pro", updated.getName());
        assertEquals("Tablet atualizado", updated.getDescription());

        HttpResponse<String> getResponse = client.send(
                HttpRequest.newBuilder(uri("/products/" + created.getId())).GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );
        assertEquals(200, getResponse.statusCode());
        Product fetched = objectMapper.readValue(getResponse.body(), Product.class);
        assertEquals("Tablet Pro", fetched.getName());
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        Product created = createProduct("Camera", "Camera de ação", "890.00");

        HttpResponse<String> deleteResponse = client.send(
                HttpRequest.newBuilder(uri("/products/" + created.getId())).DELETE().build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(204, deleteResponse.statusCode());
        assertEquals("", deleteResponse.body());

        HttpResponse<String> getResponse = client.send(
                HttpRequest.newBuilder(uri("/products/" + created.getId())).GET().build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(404, getResponse.statusCode());
        assertTrue(getResponse.body().contains("Produto não encontrado"));
    }

    @Test
    void shouldDeleteProductUsingQueryParameter() throws Exception {
        Product created = createProduct("Console", "Console portátil", "1999.99");

        HttpResponse<String> deleteResponse = client.send(
                HttpRequest.newBuilder(uri("/products?id=" + created.getId())).DELETE().build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(204, deleteResponse.statusCode());
        assertEquals("", deleteResponse.body());
    }

    @Test
    void shouldReturnBadRequestWhenDeletingWithoutId() throws Exception {
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder(uri("/products")).DELETE().build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("Identificador de produto não informado"));
    }

    @Test
    void shouldReturnBadRequestWhenDeletingWithInvalidId() throws Exception {
        HttpResponse<String> response = client.send(
                HttpRequest.newBuilder(uri("/products?id=abc")).DELETE().build(),
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("Identificador de produto inválido"));
    }

    private Product createProduct(String name, String description, String price) throws Exception {
        String payload = "{" +
                "\"name\":\"" + name + "\"," +
                "\"description\":\"" + description + "\"," +
                "\"price\":" + price +
                "}";

        HttpRequest request = HttpRequest.newBuilder(uri("/products"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        return objectMapper.readValue(response.body(), Product.class);
    }

    private URI uri(String path) {
        return URI.create("http://localhost:" + port + path);
    }
}
