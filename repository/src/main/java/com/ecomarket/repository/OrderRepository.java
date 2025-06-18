package com.ecomarket.repository;

import com.ecomarket.repository.entities.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderRepository.class);
    private final DataSource dataSource;

    public OrderRepository() {
        // DatabaseInitializer.initialize(); // Already initialized in Product Repository
        dataSource = DatabaseInitializer.getDataSource();
        LOGGER.info("OrderRepository initialized with DataSource");
    }

    public Order save(Order order) {
        String sql = order.getId() == null
                ? "INSERT INTO orders (user_id, total_price, status, created_at) VALUES (?, ?, ?, ?) RETURNING id"
                : "UPDATE orders SET user_id = ?, total_price = ?, status = ?, created_at = ? WHERE id = ?";

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, order.getUserId());
            stmt.setBigDecimal(2, order.getTotalPrice());
            stmt.setString(3, order.getStatus());
            stmt.setTimestamp(4, Timestamp.valueOf(order.getCreatedAt()));
            if (order.getId() != null) stmt.setLong(5, order.getId());

            if (order.getId() == null) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    order.setId(rs.getLong("id"));
                }
                LOGGER.debug("Persisted new order for user_id: {}", order.getUserId());
            } else {
                stmt.executeUpdate();
                LOGGER.debug("Updated order: {}", order.getId());
            }
            return order;
        } catch (SQLException e) {
            LOGGER.error("Failed to save order for user_id: {}. SQL State: {}, Error Code: {}, Message: {}",
                    order.getUserId(), e.getSQLState(), e.getErrorCode(), e.getMessage(), e);
            throw new RuntimeException("Failed to save order: " + e.getMessage(), e);
        }
    }

    public List<Order> findAll() {
        String sql = "SELECT * FROM orders";
        List<Order> orders = new ArrayList<>();
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getLong("id"));
                order.setUserId(rs.getLong("user_id"));
                order.setTotalPrice(rs.getBigDecimal("total_price"));
                order.setStatus(rs.getString("status"));
                order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                orders.add(order);
            }
            LOGGER.debug("Retrieved {} orders", orders.size());
            return orders;
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve all orders. Message: {}", e.getMessage());
            throw new RuntimeException("Failed to retrieve orders: " + e.getMessage(), e);
        }
    }

    public List<Order> findByUserId(Long userId) {
        String sql = "SELECT id, user_id, total_price, status, created_at FROM orders WHERE user_id = ?";
        List<Order> orders = new ArrayList<>();

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getLong("id"));
                order.setUserId(rs.getLong("user_id"));
                order.setTotalPrice(rs.getBigDecimal("total_price"));
                order.setStatus(rs.getString("status"));
                order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                orders.add(order);
            }
            LOGGER.debug("Retrieved {} orders for user_id: {}", orders.size(), userId);
            return orders;
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve orders for user_id: {}. Message: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve orders: " + e.getMessage(), e);
        }
    }
}
