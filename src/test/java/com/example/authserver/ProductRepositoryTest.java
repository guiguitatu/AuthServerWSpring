package com.example.authserver;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductRepositoryTest {

    private ProductRepository repository;

    @BeforeEach
    void setUp() {
        repository = new ProductRepository();
    }

    @Test
    void shouldCreateProductsWithIncrementalIds() {
        Product product = repository.save(new Product(0, "Teclado", "Teclado mecânico", new BigDecimal("350.00")));
        Product product2 = repository.save(new Product(0, "Headset", "Headset gamer", new BigDecimal("420.00")));

        assertEquals(product.getId() + 1, product2.getId());
    }

    @Test
    void shouldReturnProductById() {
        Product product = repository.save(new Product(0, "Cadeira", "Cadeira ergonômica", new BigDecimal("1200.00")));

        assertTrue(repository.findById(product.getId()).isPresent());
    }

    @Test
    void shouldListAllProducts() {
        repository.save(new Product(0, "Monitor", "Monitor 4K", new BigDecimal("2500.00")));
        repository.save(new Product(0, "Webcam", "Webcam HD", new BigDecimal("300.00")));

        assertTrue(repository.findAll().size() >= 2);
    }
}
