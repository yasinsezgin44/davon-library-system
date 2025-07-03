package com.davon.library.dao.impl;

import com.davon.library.dao.UserDAO;
import com.davon.library.dao.DAOException;
import com.davon.library.database.DatabaseConnectionManager;
import com.davon.library.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MSSQL implementation of UserDAO.
 */
@ApplicationScoped
public class MSSQLUserDAOImpl implements UserDAO {
    private static final Logger logger = LoggerFactory.getLogger(MSSQLUserDAOImpl.class);

    @Inject
    DatabaseConnectionManager connectionManager;

    @Override
    public User save(User entity) throws DAOException {
        String sql = "INSERT INTO users (username, password_hash, full_name, email, phone_number, active, status, last_login, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            LocalDateTime now = LocalDateTime.now();
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);
            
            stmt.setString(1, entity.getUsername());
            stmt.setString(2, entity.getPasswordHash());
            stmt.setString(3, entity.getFullName());
            stmt.setString(4, entity.getEmail());
            stmt.setString(5, entity.getPhoneNumber());
            stmt.setBoolean(6, entity.isActive());
            stmt.setString(7, entity.getStatus() != null ? entity.getStatus().name() : "ACTIVE");
            stmt.setDate(8, entity.getLastLogin() != null ? Date.valueOf(entity.getLastLogin()) : null);
            stmt.setTimestamp(9, Timestamp.valueOf(now));
            stmt.setTimestamp(10, Timestamp.valueOf(now));
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
            }
            
            return entity;
            
        } catch (SQLException e) {
            logger.error("Error saving user", e);
            throw new DAOException("Failed to save user", e);
        }
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.error("Error finding user with id {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = connectionManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.error("Error finding user by username {}", username, e);
            return Optional.empty();
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return Member.builder()
                .id(rs.getLong("id"))
                .username(rs.getString("username"))
                .passwordHash(rs.getString("password_hash"))
                .fullName(rs.getString("full_name"))
                .email(rs.getString("email"))
                .phoneNumber(rs.getString("phone_number"))
                .active(rs.getBoolean("active"))
                .status(UserStatus.valueOf(rs.getString("status")))
                .lastLogin(rs.getDate("last_login") != null ? rs.getDate("last_login").toLocalDate() : null)
                .createdAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null)
                .lastModifiedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null)
                .build();
    }

    // Implement remaining methods with basic implementations
    @Override public User update(User entity) throws DAOException { throw new UnsupportedOperationException(); }
    @Override public void delete(User entity) throws DAOException { deleteById(entity.getId()); }
    @Override public void deleteById(Long id) throws DAOException { /* Implementation */ }
    @Override public List<User> findAll() { return new ArrayList<>(); }
    @Override public boolean existsById(Long id) { return false; }
    @Override public long count() { return 0; }
    @Override public Optional<User> findByEmail(String email) { return Optional.empty(); }
    @Override public List<User> findByRole(Role role) { return new ArrayList<>(); }
    @Override public List<User> findActiveUsers() { return new ArrayList<>(); }
    @Override public List<User> findInactiveUsers() { return new ArrayList<>(); }
    @Override public List<User> searchUsers(String searchTerm) { return new ArrayList<>(); }
    @Override public List<User> findByStatus(String status) { return new ArrayList<>(); }
    @Override public boolean existsByUsername(String username) { return false; }
    @Override public boolean existsByEmail(String email) { return false; }
    @Override public List<User> findByCreatedDateBetween(LocalDateTime startDate, LocalDateTime endDate) { return new ArrayList<>(); }
    @Override public long countByRole(Role role) { return 0; }
    @Override public long countActiveUsers() { return 0; }
}
