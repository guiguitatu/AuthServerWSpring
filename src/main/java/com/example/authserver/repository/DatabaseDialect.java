package com.example.authserver.repository;

import java.util.Locale;

public enum DatabaseDialect {
    MYSQL("""
            CREATE TABLE IF NOT EXISTS products (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(255) NOT NULL,
                description TEXT NOT NULL,
                price DECIMAL(19, 2) NOT NULL
            )
            """),
    SQLITE("""
            CREATE TABLE IF NOT EXISTS products (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                description TEXT NOT NULL,
                price NUMERIC NOT NULL
            )
            """);

    private final String createTableStatement;

    DatabaseDialect(String createTableStatement) {
        this.createTableStatement = createTableStatement;
    }

    public String createTableStatement() {
        return createTableStatement;
    }

    public static DatabaseDialect fromString(String value) {
        if (value == null || value.isBlank()) {
            return MYSQL;
        }
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "mysql" -> MYSQL;
            case "sqlite" -> SQLITE;
            default -> throw new IllegalArgumentException("Unsupported database adapter: " + value);
        };
    }
}
