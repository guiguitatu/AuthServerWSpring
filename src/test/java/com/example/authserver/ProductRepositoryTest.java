package com.example.authserver;

import com.example.authserver.domain.Product;
import com.example.authserver.repository.DatabaseDialect;
import com.example.authserver.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductRepositoryTest {

    private ProductRepository repository;
    private String jdbcUrl;

    @BeforeEach
    void setUp() throws SQLException {
        jdbcUrl = "jdbc:h2:mem:product-repository-test-" + UUID.randomUUID() + ";MODE=MySQL;DB_CLOSE_DELAY=-1";
        repository = new ProductRepository(jdbcUrl, "sa", "", DatabaseDialect.MYSQL);
        recreateSchema();
    }

    @Test
    void shouldCreateProductsWithIncrementalIds() {
        Product product = repository.save(new Product(null, "Teclado", "Teclado mecânico", new BigDecimal("350.00")));
        Product product2 = repository.save(new Product(null, "Headset", "Headset gamer", new BigDecimal("420.00")));

        assertEquals(product.getId() + 1, product2.getId());
    }

    @Test
    void shouldReturnProductById() {
        Product product = repository.save(new Product(null, "Cadeira", "Cadeira ergonômica", new BigDecimal("1200.00")));

        assertTrue(repository.findById(product.getId()).isPresent());
    }

    @Test
    void shouldListAllProducts() {
        repository.save(new Product(null, "Monitor", "Monitor 4K", new BigDecimal("2500.00")));
        repository.save(new Product(null, "Webcam", "Webcam HD", new BigDecimal("300.00")));

        assertEquals(2, repository.findAll().size());
    }

    @Test
    void shouldUpdateExistingProduct() {
        Product product = repository.save(new Product(null, "Console", "Console de videogame", new BigDecimal("3500.00")));

        repository.save(new Product(product.getId(), "Console Pro", "Console atualizado", new BigDecimal("4200.00")));

        Product updated = repository.findById(product.getId()).orElseThrow();
        assertEquals("Console Pro", updated.getName());
        assertEquals(new BigDecimal("4200.00"), updated.getPrice());
    }

    @Test
    void shouldRemoveProductById() {
        Product product = repository.save(new Product(null, "Impressora", "Impressora laser", new BigDecimal("1100.00")));

        assertTrue(repository.deleteById(product.getId()));
        assertTrue(repository.findById(product.getId()).isEmpty());
    }

    @Test
    void shouldReturnFalseWhenDeletingUnknownProduct() {
        assertFalse(repository.deleteById(9999));
    }

    private void recreateSchema() throws SQLException {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, "sa", "");
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS products");
            statement.executeUpdate("""
                    CREATE TABLE products (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        description TEXT NOT NULL,
                        price DECIMAL(19, 2) NOT NULL
                    )
                    """);
        }
    }
}
