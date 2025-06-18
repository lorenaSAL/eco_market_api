package com.ecomarket.repository;

import com.ecomarket.repository.entities.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductRepository.class);
    private final DataSource dataSource;

    public ProductRepository() {
        DatabaseInitializer.initialize();
        this.dataSource = DatabaseInitializer.getDataSource();
        LOGGER.info("ProductRepository initialized with DataSource");
    }

    public Product save(Product product) {
        String sql = product.getId() == null
                ? "INSERT INTO products (name, description, price, category, carbon_saving) VALUES (?, ?, ?, ?, ?) RETURNING id"
                : "UPDATE products SET name = ?, description = ?, price = ?, category = ?, carbon_saving = ? WHERE id = ?";

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, product.getName());
            stmt.setString(2, product.getDescription());
            stmt.setBigDecimal(3, product.getPrice());
            stmt.setString(4, product.getCategory());
            stmt.setBigDecimal(5, product.getCarbon_saving());
            if (product.getId() != null) {
                stmt.setLong(6, product.getId());
            }

            if (product.getId() == null) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    product.setId(rs.getLong("id"));
                }
                LOGGER.debug("Persisted new Product: {}", product.getName());
            } else {
                stmt.executeUpdate();
                LOGGER.debug("Updated Product: {}", product.getName());
            }
            return product;
        } catch (SQLException e) {
            LOGGER.error("Failed to save product: {}. SQL State: {}, Error Code: {}, Message: {}",
                    product.getName(), e.getSQLState(), e.getErrorCode(), e.getMessage(), e);
            throw new RuntimeException("Failed to save product: " + e.getMessage(), e);
        }

    }

    public Optional<Product> findById(Long id) {
        String sql = "SELECT id, name, description, price, category, carbon_saving FROM products WHERE id = ?";

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Product product = new Product();
                product.setId(rs.getLong("id"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setCategory(rs.getString("category"));
                product.setCarbon_saving(rs.getBigDecimal("carbon_saving"));
                LOGGER.debug("Retrieved product by ID: {}", product.getId());
                return Optional.of(product);
            }
            LOGGER.debug("Product not found: {}", id);
            return Optional.empty();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve product: {}. Message: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve product " + e.getMessage(), e);
        }
    }

    public List<Product> findAll() {
        String sql = "SELECT * FROM products";
        List<Product> products = new ArrayList<>();

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getLong("id"));
                product.setName(rs.getString("name"));
                product.setDescription(rs.getString("description"));
                product.setPrice(rs.getBigDecimal("price"));
                product.setCategory(rs.getString("category"));
                product.setCarbon_saving(rs.getBigDecimal("carbon_saving"));
                products.add(product);
            }
            LOGGER.debug("Retrieved {} products", products.size());
            return products;
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve all products", e);
            throw new RuntimeException("Failed to retrieve products", e);
        }
    }
}
