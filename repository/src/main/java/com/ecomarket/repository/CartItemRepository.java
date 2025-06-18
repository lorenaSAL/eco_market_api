package com.ecomarket.repository;

import com.ecomarket.repository.entities.CartItem;
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

public class CartItemRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(CartItemRepository.class);
    private final DataSource dataSource;

    public CartItemRepository() {
        // DatabaseInitializer.initialize(); // Already initialized in ProductRepository
        this.dataSource = DatabaseInitializer.getDataSource();
        LOGGER.info("CartItemRepository initialized with DataSource");
    }

    public CartItem save(CartItem cartItem) {
        String sql = cartItem.getId() == null
                ? "INSERT INTO cart_items (cart_id, product_id, quantity) VALUES (?, ?, ?) RETURNING id"
                : "UPDATE cart_items SET cart_id = ?, product_id = ?, quantity = ? WHERE id = ?";

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, cartItem.getCartId());
            stmt.setLong(2, cartItem.getProductId());
            stmt.setInt(3, cartItem.getQuantity());
            if (cartItem.getId() != null) stmt.setLong(4, cartItem.getId());

            if (cartItem.getId() == null) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    cartItem.setId(rs.getLong("id"));
                }
                LOGGER.debug("Persisted new cart item for cart_id: {}", cartItem.getCartId());
            } else {
                stmt.executeUpdate();
                LOGGER.debug("Updated cart item: {}", cartItem.getId());
            }
            return cartItem;
        } catch (SQLException e) {
            LOGGER.error("Failed to save cart item for cart id: {}. SQL State: {}, Error Code: {}, Message: {}",
                    cartItem.getCartId(), e.getSQLState(), e.getErrorCode(), e.getMessage(), e);
            throw new RuntimeException("Failed to save cart item: " + e.getMessage(), e);
        }
    }

    public Optional<CartItem> findById(Long id) {
        String sql = "SELECT id, cart_id, product_id, quantity FROM cart_items WHERE id = ?";

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                CartItem cartItem = new CartItem();
                cartItem.setId(rs.getLong("id"));
                cartItem.setCartId(rs.getLong("cart_id"));
                cartItem.setProductId(rs.getLong("product_id"));
                cartItem.setQuantity(rs.getInt("quantity"));
                LOGGER.info("Retrieved cart item by id: {}", cartItem.getCartId());
                return Optional.of(cartItem);
            }
            LOGGER.debug("Cart item not found: {}", id);
            return Optional.empty();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve cart item: {}. Message: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve cart item: " + e.getMessage(), e);
        }
    }

    public List<CartItem> findAll() {
        String sql = "SELECT * FROM cart_items";
        List<CartItem> cartItems = new ArrayList<>();
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                CartItem cartItem = new CartItem();
                cartItem.setId(rs.getLong("id"));
                cartItem.setCartId(rs.getLong("cart_id"));
                cartItem.setProductId(rs.getLong("product_id"));
                cartItem.setQuantity(rs.getInt("quantity"));
                cartItems.add(cartItem);
            }
            LOGGER.debug("Retrieved {} cart items", cartItems.size());
            return cartItems;
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve all cart items. Message: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve cart items: " + e.getMessage(), e);
        }
    }

    public List<CartItem> findByCartId(Long cartId) {
        String sql = "SELECT id, cart_id, product_id, quantity FROM cart_items WHERE cart_id = ?";
        List<CartItem> cartItems = new ArrayList<>();
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, cartId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                CartItem cartItem = new CartItem();
                cartItem.setId(rs.getLong("id"));
                cartItem.setCartId(rs.getLong("cart_id"));
                cartItem.setProductId(rs.getLong("product_id"));
                cartItem.setQuantity(rs.getInt("quantity"));
                cartItems.add(cartItem);
            }
            LOGGER.debug("Retrieved {} cart items for cart_id: {}", cartItems, cartId);
            return cartItems;
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve cart items for cart_id: {}. Message: {}", cartId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve cart items: " + e.getMessage(), e);
        }
    }

    public void delete(Long id) {
        String sql = "DELETE FROM cart_items WHERE id = ?";
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.debug("Deleted cart item: {}", id);
            } else {
                LOGGER.warn("Cart item not found for deletion: {}", id);
            }
        } catch (SQLException e) {
            LOGGER.error("Failed to delete cart item: {}. Message: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to delete cart item: " + e.getMessage(), e);
        }
    }
}
