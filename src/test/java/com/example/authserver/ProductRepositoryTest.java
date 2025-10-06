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

    @Test
    void shouldUpdateExistingProduct() {
        Product product = repository.save(new Product(0, "Console", "Console de videogame", new BigDecimal("3500.00")));

        repository.save(new Product(product.getId(), "Console Pro", "Console atualizado", new BigDecimal("4200.00")));

        Product updated = repository.findById(product.getId()).orElseThrow();
        assertEquals("Console Pro", updated.getName());
        assertEquals(new BigDecimal("4200.00"), updated.getPrice());
    }

    @Test
    void shouldRemoveProductById() {
        Product product = repository.save(new Product(0, "Impressora", "Impressora laser", new BigDecimal("1100.00")));

        assertTrue(repository.deleteById(product.getId()));
        assertTrue(repository.findById(product.getId()).isEmpty());
    }

    @Test
    void shouldReturnFalseWhenDeletingUnknownProduct() {
        assertFalse(repository.deleteById(9999));
    }
}
