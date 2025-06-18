package com.ecomarket.repository;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseInitializer.class);
    private static final DataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/ecomarket");
        config.setUsername("myuser");
        config.setPassword("mypassword");
        config.setDriverClassName("org.postgresql.Driver");
        dataSource = new HikariDataSource(config);
    }

    private static final String INIT_SQL = """
            CREATE TABLE IF NOT EXISTS products (
                id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                name VARCHAR(255),
                description TEXT,
                price DECIMAL(10, 2),
                category VARCHAR(50),
                carbon_saving DECIMAL(10, 2)
            );
    
            CREATE TABLE IF NOT EXISTS users (
                id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                username VARCHAR(50),
                email VARCHAR(255),
                password VARCHAR(255),
                role VARCHAR(20)
            );
    
            CREATE TABLE IF NOT EXISTS carts (
                id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                user_id BIGINT REFERENCES users(id)
            );
    
            CREATE TABLE IF NOT EXISTS cart_items (
                id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                cart_id BIGINT REFERENCES carts(id),
                product_id BIGINT REFERENCES products(id),
                quantity INT
            );
    
            CREATE TABLE IF NOT EXISTS orders (
                id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                user_id BIGINT REFERENCES users(id),
                total_price DECIMAL(10, 2),
                status VARCHAR(20),
                created_at TIMESTAMP
            );
            """;

    public static void initialize() {
        try (
                Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement()
        ) {
            LOGGER.info("Initializing database schema");
            stmt.execute(INIT_SQL);
            LOGGER.info("Database schema initialized successfully");
        } catch (SQLException e) {
            LOGGER.error("Failed to initialize database schema", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
