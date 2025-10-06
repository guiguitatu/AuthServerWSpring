package com.example.authserver;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductRepositoryTest {

    private EntityManagerFactory entityManagerFactory;
    private ProductRepository repository;

    @BeforeEach
    void setUp() {
        entityManagerFactory = Persistence.createEntityManagerFactory("authServerPU", Map.of(
                "jakarta.persistence.jdbc.url", "jdbc:h2:mem:product-repository-test-" + UUID.randomUUID() + ";MODE=LEGACY"
        ));
        repository = new ProductRepository(entityManagerFactory);
    }

    @AfterEach
    void tearDown() {
        entityManagerFactory.close();
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
}
