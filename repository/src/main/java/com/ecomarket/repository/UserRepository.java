package com.ecomarket.repository;

import com.ecomarket.repository.entities.User;
import org.mindrot.jbcrypt.BCrypt;
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
        LOGGER.debug("Saving user: {}", user.getEmail());
        if (user.getId() == null) {
            // Hash password before saving
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            String sql = "INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?) RETURNING id";
            try (
                    Connection conn = dataSource.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(sql)
            ) {
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, hashedPassword);
                stmt.setString(4, user.getRole());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    user.setId(rs.getLong("id"));
                    user.setPassword(rs.getString("password"));
                    LOGGER.info("User saved: {}", user.getEmail());
                    return user;
                }
                throw new RuntimeException("Failed to retrieve generated ID");
            } catch (SQLException e) {
                LOGGER.error("Failed to save user: {}", user.getEmail(), e);
                throw new RuntimeException("Failed to save user: " + e.getMessage(), e);
            }
        } else {
            // Update existing user
            String sql = "UPDATE users SET username = ?, email = ?, password = ?, role = ? WHERE id = ?";
            try (
                    Connection conn = dataSource.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(sql)
            ) {
                String hashedPassword = user.getPassword().startsWith("$2a$")
                        ? user.getPassword()
                        : BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
                stmt.setString(1, user.getUsername());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, hashedPassword);
                stmt.setString(4, user.getRole());
                stmt.setLong(5, user.getId());
                stmt.executeUpdate();
                user.setPassword(hashedPassword);
                LOGGER.info("user updated: {}", user.getEmail());
                return user;
            } catch (SQLException e) {
                LOGGER.error("Failed to update user: {}", user.getEmail(), e);
                throw new RuntimeException("Failed to update user: " + e.getMessage(), e);
            }
        }
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, username, email, password, role FROM users WHERE email = ?";
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                LOGGER.info("User found by email: {}", user.getEmail());
                return Optional.of(user);
            }
            LOGGER.info("No user found with email: {}", email);
            return Optional.empty();
        } catch (SQLException e) {
            LOGGER.error("Failed to find user by email: {}", email, e);
            throw new RuntimeException("Failed to find user by email: " + e.getMessage(), e);
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
