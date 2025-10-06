package com.example.authserver;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ProductRepository {

    private final Map<Integer, Product> products = new ConcurrentHashMap<>();
    private final AtomicInteger sequence = new AtomicInteger();

    public ProductRepository() {
        save(new Product(0, "Notebook", "Notebook com 16GB RAM", new BigDecimal("5500.00")));
        save(new Product(0, "Mouse", "Mouse sem fio", new BigDecimal("80.50")));
    }

    public Collection<Product> findAll() {
        return new ArrayList<>(products.values());
    }

    public Optional<Product> findById(int id) {
        return Optional.ofNullable(products.get(id));
    }

    public Product save(Product product) {
        if (product.getId() == 0) {
            product.setId(sequence.incrementAndGet());
        }
        products.put(product.getId(), product);
        return product;
    }

}
