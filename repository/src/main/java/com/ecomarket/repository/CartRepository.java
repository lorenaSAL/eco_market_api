package com.ecomarket.repository;

import com.ecomarket.repository.entities.Cart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CartRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(CartRepository.class);
    private final DataSource dataSource;

    public CartRepository() {
        // DatabaseInitializer.initialize(); // Already initialized in ProductRepository
        dataSource = DatabaseInitializer.getDataSource();
        LOGGER.info("CartRepository initialized with DataSource");
    }

    public Cart save(Cart cart) {
        String sql = cart.getId() == null
                ? "INSERT INTO carts (user_id) VALUES (?) RETURNING id"
                : "UPDATE carts SET user_id = ? WHERE id = ?";

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, cart.getUserId());
            if (cart.getId() != null) stmt.setLong(2, cart.getId());

            if (cart.getId() == null) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    cart.setId(rs.getLong("id"));
                }
                LOGGER.debug("Persisted new cart for user_id: {}", cart.getUserId());
            } else {
                stmt.executeUpdate();
                LOGGER.debug("Updated cart: {}", cart.getId());
            }
            return cart;
        } catch (SQLException e) {
            LOGGER.error("Failed to save cart for user id: {}. SQL State: {}, Error Code: {}, Message: {}",
                    cart.getUserId(), e.getSQLState(), e.getErrorCode(), e.getMessage(), e);
            throw new RuntimeException("Failed to save cart: " + e.getMessage(), e);
        }
    }

    public Optional<Cart> findById(Long id) {
        String sql = "SELECT id, user_id FROM carts WHERE id = ?";

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Cart cart = new Cart();
                cart.setId(rs.getLong("id"));
                cart.setUserId(rs.getLong("user_id"));
                LOGGER.debug("Retrieved cart by ID: {}", cart.getId());
                return Optional.of(cart);
            }
            LOGGER.debug("Cart not foud: {}", id);
            return Optional.empty();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve cart: {}. Message: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve cart: " + e.getMessage(), e);
        }
    }

    public List<Cart> findAll() {
        String sql = "SELECT * FROM carts";
        List<Cart> carts = new ArrayList<>();
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Cart cart = new Cart();
                cart.setId(rs.getLong("id"));
                cart.setUserId(rs.getLong("user_id"));
                carts.add(cart);
            }
            LOGGER.debug("Retrieved {} carts", carts.size());
            return carts;
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve all carts. Message: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve carts: " + e.getMessage(), e);
        }
    }

    public Optional<Cart> findByUserId(Long userId) {
        String sql = "SELECT id, user_id FROM carts WHERE user_id = ?";

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Cart cart = new Cart();
                cart.setId(rs.getLong("id"));
                cart.setUserId(rs.getLong("user_id"));
                LOGGER.debug("Retrieved cart for user_id: {}", cart.getUserId());
                return Optional.of(cart);
            }
            LOGGER.debug("Cart not found for user_id: {}", userId);
            return Optional.empty();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve cart for user_id: {}. Message: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve cart" + e.getMessage(), e);
        }
    }
}
