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
import java.time.LocalDate;
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
        String sql = "INSERT INTO users (username, password_hash, email, full_name, phone_number, active, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            LocalDateTime now = LocalDateTime.now();
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);

            stmt.setString(1, entity.getUsername());
            stmt.setString(2, entity.getPasswordHash());
            stmt.setString(3, entity.getEmail());
            stmt.setString(4, entity.getFullName());
            stmt.setString(5, entity.getPhoneNumber());
            stmt.setBoolean(6, entity.isActive());
            stmt.setString(7,
                    entity.getStatus() != null ? entity.getStatus().toString() : UserStatus.ACTIVE.toString());
            stmt.setTimestamp(8, Timestamp.valueOf(now));
            stmt.setTimestamp(9, Timestamp.valueOf(now));

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }

            // If this is a Member, also insert into members table
            if (entity instanceof Member) {
                insertMemberRecord((Member) entity, conn);
            }

            return entity;
        } catch (SQLException e) {
            logger.error("Error saving user with username: {}", entity.getUsername(), e);
            throw new DAOException("Failed to save user", e);
        }
    }

    private void insertMemberRecord(Member member, Connection conn) throws SQLException {
        String memberSql = "INSERT INTO members (user_id, membership_start_date, membership_end_date, address, fine_balance) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement memberStmt = conn.prepareStatement(memberSql)) {
            memberStmt.setLong(1, member.getId());
            memberStmt.setDate(2,
                    member.getMembershipStartDate() != null ? Date.valueOf(member.getMembershipStartDate())
                            : Date.valueOf(LocalDate.now()));
            memberStmt.setDate(3,
                    member.getMembershipEndDate() != null ? Date.valueOf(member.getMembershipEndDate()) : null);
            memberStmt.setString(4, member.getAddress());
            memberStmt.setDouble(5, member.getFineBalance());

            memberStmt.executeUpdate();
            logger.debug("Successfully inserted member record for user ID: {}", member.getId());
        }
    }

    @Override
    public User update(User entity) throws DAOException {
        String sql = "UPDATE users SET username = ?, password_hash = ?, full_name = ?, email = ?, phone_number = ?, active = ?, status = ?, last_login = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            entity.setLastModifiedAt(LocalDateTime.now());

            stmt.setString(1, entity.getUsername());
            stmt.setString(2, entity.getPasswordHash());
            stmt.setString(3, entity.getFullName());
            stmt.setString(4, entity.getEmail());
            stmt.setString(5, entity.getPhoneNumber());
            stmt.setBoolean(6, entity.isActive());
            stmt.setString(7, entity.getStatus() != null ? entity.getStatus().name() : "ACTIVE");
            stmt.setDate(8, entity.getLastLogin() != null ? Date.valueOf(entity.getLastLogin()) : null);
            stmt.setTimestamp(9, Timestamp.valueOf(entity.getLastModifiedAt()));
            stmt.setLong(10, entity.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Updating user failed, no rows affected.");
            }

            // If it's a Member, also update the members table
            if (entity instanceof Member) {
                updateMemberRecord((Member) entity, conn);
            }

            return entity;

        } catch (SQLException e) {
            logger.error("Error updating user", e);
            throw new DAOException("Failed to update user", e);
        }
    }

    private void updateMemberRecord(Member member, Connection conn) throws SQLException {
        String memberSql = "UPDATE members SET address = ?, membership_start_date = ?, membership_end_date = ?, fine_balance = ? WHERE user_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(memberSql)) {
            stmt.setString(1, member.getAddress());
            stmt.setDate(2,
                    member.getMembershipStartDate() != null ? Date.valueOf(member.getMembershipStartDate()) : null);
            stmt.setDate(3, member.getMembershipEndDate() != null ? Date.valueOf(member.getMembershipEndDate()) : null);
            stmt.setDouble(4, member.getFineBalance());
            stmt.setLong(5, member.getId());

            int memberRowsAffected = stmt.executeUpdate();
            if (memberRowsAffected == 0) {
                logger.warn("No member record found to update for user ID: {}", member.getId());
            } else {
                logger.debug("Successfully updated member record for user ID: {}", member.getId());
            }
        }
    }

    @Override
    public void delete(User entity) throws DAOException {
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(Long id) throws DAOException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Deleting user failed, no rows affected.");
            }

        } catch (SQLException e) {
            logger.error("Error deleting user with id {}", id, e);
            throw new DAOException("Failed to delete user", e);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY id";
        List<User> users = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            logger.error("Error finding all users", e);
        }

        return users;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM users WHERE id = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            logger.error("Error checking if user exists with id {}", id, e);
            return false;
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM users";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            logger.error("Error counting users", e);
        }

        return 0;
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

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToUser(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding user by email {}", email, e);
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            logger.error("Error checking if username exists: {}", username, e);
            return false;
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            logger.error("Error checking if email exists: {}", email, e);
            return false;
        }
    }

    @Override
    public List<User> findByRole(Role role) {
        // For now, return all users since we don't have role-specific logic implemented
        return findAll();
    }

    @Override
    public List<User> findActiveUsers() {
        String sql = "SELECT * FROM users WHERE active = 1 ORDER BY id";
        List<User> users = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            logger.error("Error finding active users", e);
        }

        return users;
    }

    @Override
    public List<User> findInactiveUsers() {
        String sql = "SELECT * FROM users WHERE active = 0 ORDER BY id";
        List<User> users = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }

        } catch (SQLException e) {
            logger.error("Error finding inactive users", e);
        }

        return users;
    }

    @Override
    public List<User> searchUsers(String searchTerm) {
        String sql = "SELECT * FROM users WHERE username LIKE ? OR full_name LIKE ? OR email LIKE ? ORDER BY id";
        List<User> users = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error searching users", e);
        }

        return users;
    }

    @Override
    public List<User> findByStatus(String status) {
        String sql = "SELECT * FROM users WHERE status = ? ORDER BY id";
        List<User> users = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding users by status", e);
        }

        return users;
    }

    @Override
    public List<User> findByCreatedDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = "SELECT * FROM users WHERE created_at BETWEEN ? AND ? ORDER BY created_at";
        List<User> users = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(mapResultSetToUser(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding users by created date", e);
        }

        return users;
    }

    @Override
    public long countByRole(Role role) {
        // For now, return total count since role logic is not implemented
        return count();
    }

    @Override
    public long countActiveUsers() {
        String sql = "SELECT COUNT(*) FROM users WHERE active = 1";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            logger.error("Error counting active users", e);
        }

        return 0;
    }

    @Override
    public void clearAll() throws DAOException {
        String sql = "DELETE FROM users";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Error clearing all users", e);
            throw new DAOException("Failed to clear all users", e);
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
                .createdAt(
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null)
                .lastModifiedAt(
                        rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null)
                .build();
    }
}
