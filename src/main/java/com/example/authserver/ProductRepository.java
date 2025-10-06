package com.example.authserver;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ProductRepository {

    private final EntityManagerFactory entityManagerFactory;

    public ProductRepository(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void initializeSampleData() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Long count = entityManager.createQuery("select count(p) from Product p", Long.class)
                    .getSingleResult();
            if (count == 0) {
                entityManager.persist(new Product(null, "Notebook", "Notebook com 16GB RAM", new BigDecimal("5500.00")));
                entityManager.persist(new Product(null, "Mouse", "Mouse sem fio", new BigDecimal("80.50")));
            }
            transaction.commit();
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        } finally {
            entityManager.close();
        }
    }

    public Collection<Product> findAll() {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            List<Product> result = entityManager.createQuery("select p from Product p order by p.id", Product.class)
                    .getResultList();
            result.forEach(entityManager::detach);
            return result;
        } finally {
            entityManager.close();
        }
    }

    public Optional<Product> findById(int id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            Product product = entityManager.find(Product.class, id);
            if (product != null) {
                entityManager.detach(product);
            }
            return Optional.ofNullable(product);
        } finally {
            entityManager.close();
        }
    }

    public Product save(Product product) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Product managed;
            if (product.getId() == null) {
                entityManager.persist(product);
                entityManager.flush();
                managed = product;
            } else {
                managed = entityManager.merge(product);
            }
            transaction.commit();
            entityManager.detach(managed);
            return managed;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        } finally {
            entityManager.close();
        }
    }

    public boolean deleteById(int id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Product product = entityManager.find(Product.class, id);
            if (product == null) {
                transaction.commit();
                return false;
            }
            entityManager.remove(product);
            transaction.commit();
            return true;
        } catch (RuntimeException exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        } finally {
            entityManager.close();
        }
    }
}
