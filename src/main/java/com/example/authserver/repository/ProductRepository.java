package com.example.authserver.repository;

import com.example.authserver.domain.Product;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ProductRepository {

    private final String jdbcUrl;
    private final String username;
    private final String password;

    public ProductRepository(String jdbcUrl, String username, String password) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
    }

    public void initializeSampleData() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS products (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL,
                        description TEXT NOT NULL,
                        price DECIMAL(19, 2) NOT NULL
                    )
                    """);

            try (ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM products")) {
                if (resultSet.next() && resultSet.getLong(1) == 0L) {
                    try (PreparedStatement insert = connection.prepareStatement(
                            "INSERT INTO products (name, description, price) VALUES (?, ?, ?)")) {
                        insert.setString(1, "Notebook");
                        insert.setString(2, "Notebook com 16GB RAM");
                        insert.setBigDecimal(3, new BigDecimal("5500.00"));
                        insert.executeUpdate();

                        insert.setString(1, "Mouse");
                        insert.setString(2, "Mouse sem fio");
                        insert.setBigDecimal(3, new BigDecimal("80.50"));
                        insert.executeUpdate();
                    }
                }
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to initialize sample data", exception);
        }
    }

    public Collection<Product> findAll() {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, name, description, price FROM products ORDER BY id")) {
            List<Product> products = new ArrayList<>();
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    products.add(mapRow(resultSet));
                }
            }
            return products;
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to list products", exception);
        }
    }

    public Optional<Product> findById(int id) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT id, name, description, price FROM products WHERE id = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to fetch product with id " + id, exception);
        }
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            return insert(product);
        }
        return update(product);
    }

    public boolean deleteById(int id) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM products WHERE id = ?")) {
            statement.setInt(1, id);
            int rows = statement.executeUpdate();
            return rows > 0;
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to delete product with id " + id, exception);
        }
    }

    private Connection getConnection() throws SQLException {
        return java.sql.DriverManager.getConnection(jdbcUrl, username, password);
    }

    private Product insert(Product product) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO products (name, description, price) VALUES (?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setBigDecimal(3, product.getPrice());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Product(generatedKeys.getInt(1),
                            product.getName(),
                            product.getDescription(),
                            product.getPrice());
                }
                throw new RuntimeException("Failed to retrieve generated id for product");
            }
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to save product", exception);
        }
    }

    private Product update(Product product) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE products SET name = ?, description = ?, price = ? WHERE id = ?")) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setBigDecimal(3, product.getPrice());
            statement.setInt(4, product.getId());

            int rows = statement.executeUpdate();
            if (rows == 0) {
                throw new IllegalArgumentException("Product with id " + product.getId() + " does not exist");
            }
            return product;
        } catch (SQLException exception) {
            throw new RuntimeException("Failed to update product with id " + product.getId(), exception);
        }
    }

    private Product mapRow(ResultSet resultSet) throws SQLException {
        Product product = new Product();
        product.setId(resultSet.getInt("id"));
        product.setName(resultSet.getString("name"));
        product.setDescription(resultSet.getString("description"));
        product.setPrice(resultSet.getBigDecimal("price"));
        return product;
    }
}
