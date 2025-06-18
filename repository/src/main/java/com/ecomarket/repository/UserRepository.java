package com.ecomarket.repository;

import com.ecomarket.repository.entities.User;
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

public class UserRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserRepository.class);
    private final DataSource dataSource;

    public UserRepository() {
        // DatabaseInitializer.initialize(); // Already initialized in ProductRepository
        this.dataSource = DatabaseInitializer.getDataSource();
        LOGGER.info("UserRepository initialized with DataSource");
    }

    public User save(User user) {
        String sql = user.getId() == null
                ? "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?) RETURNING id"
                : "UPDATE users SET username = ?, email = ?, password = ?, role = ? WHERE id = ?";

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getRole());
            if (user.getId() != null) stmt.setLong(5, user.getId());

            if (user.getId() == null) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    user.setId(rs.getLong("id"));
                }
                LOGGER.debug("Persisted new user: {}", user.getUsername());
            } else {
                stmt.executeUpdate();
                LOGGER.debug("Updated user: {}", user.getUsername());
            }
            return user;
        } catch (SQLException e) {
            LOGGER.error("Failed to save user: {}. SQL State: {}, Error Code: {}, Message: {}",
                    user.getUsername(), e.getSQLState(), e.getErrorCode(), e.getMessage(), e);
            throw new RuntimeException("Failed to save user: " + e.getMessage(), e);
        }
    }

    public Optional<User> findById(Long id) {
        String sql = "SELECT id, username, email, password, role FROM users WHERE id = ?";

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)
        ) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                LOGGER.debug("Retrieved user by ID: {}", user.getId());
                return Optional.of(user);
            }
            LOGGER.debug("User not found: {}", id);
            return Optional.empty();
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve user: {}. Message: {}", id, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve user: " + e.getMessage(), e);
        }
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                users.add(user);
            }
            LOGGER.debug("Retrieved {} users", users.size());
            return users;
        } catch (SQLException e) {
            LOGGER.error("Failed to retrieve all users", e);
            throw new RuntimeException("Failed to retrieve users", e);
        }
    }
}
