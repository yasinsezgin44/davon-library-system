package com.davon.library.dao.impl;

import com.davon.library.dao.FineDAO;
import com.davon.library.dao.DAOException;
import com.davon.library.database.DatabaseConnectionManager;
import com.davon.library.model.Fine;
import com.davon.library.model.Member;
import com.davon.library.model.FineReason;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MSSQLFineDAOImpl implements FineDAO {
    private static final Logger logger = LoggerFactory.getLogger(MSSQLFineDAOImpl.class);

    @Inject
    DatabaseConnectionManager connectionManager;

    @Override
    public Optional<Fine> findById(Long id) {
        String sql = "SELECT * FROM fines WHERE id = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToFine(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding fine with id {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public Fine save(Fine fine) throws DAOException {
        String sql = "INSERT INTO fines (member_id, amount, reason, issue_date, due_date, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            LocalDateTime now = LocalDateTime.now();
            fine.setCreatedAt(now);
            fine.setUpdatedAt(now);

            stmt.setLong(1, fine.getMember() != null ? fine.getMember().getId() : null);
            stmt.setBigDecimal(2, BigDecimal.valueOf(fine.getAmount()));
            stmt.setString(3, fine.getReason().name());
            stmt.setDate(4, fine.getIssueDate() != null ? Date.valueOf(fine.getIssueDate()) : null);
            stmt.setDate(5, fine.getDueDate() != null ? Date.valueOf(fine.getDueDate()) : null);
            stmt.setString(6, fine.getStatus().name());
            stmt.setTimestamp(7, Timestamp.valueOf(now));
            stmt.setTimestamp(8, Timestamp.valueOf(now));

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Creating fine failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    fine.setId(generatedKeys.getLong(1));
                }
            }

            return fine;

        } catch (SQLException e) {
            logger.error("Error saving fine", e);
            throw new DAOException("Failed to save fine", e);
        }
    }

    @Override
    public List<Fine> findByMember(Member member) {
        String sql = "SELECT * FROM fines WHERE member_id = ?";
        List<Fine> fines = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, member.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    fines.add(mapResultSetToFine(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding fines by member {}", member.getId(), e);
        }

        return fines;
    }

    @Override
    public List<Fine> findUnpaidFinesByMember(Member member) {
        String sql = "SELECT * FROM fines WHERE member_id = ? AND status = 'PENDING'";
        List<Fine> fines = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, member.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    fines.add(mapResultSetToFine(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding unpaid fines by member {}", member.getId(), e);
        }

        return fines;
    }

    @Override
    public double getTotalUnpaidAmount(Member member) {
        String sql = "SELECT SUM(amount) FROM fines WHERE member_id = ? AND status = 'PENDING'";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, member.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }

        } catch (SQLException e) {
            logger.error("Error calculating total unpaid amount for member {}", member.getId(), e);
        }

        return 0.0;
    }

    @Override
    public Fine update(Fine fine) throws DAOException {
        String sql = "UPDATE fines SET member_id = ?, amount = ?, reason = ?, issue_date = ?, due_date = ?, status = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            fine.setUpdatedAt(LocalDateTime.now());

            stmt.setLong(1, fine.getMember() != null ? fine.getMember().getId() : null);
            stmt.setBigDecimal(2, BigDecimal.valueOf(fine.getAmount()));
            stmt.setString(3, fine.getReason().name());
            stmt.setDate(4, fine.getIssueDate() != null ? Date.valueOf(fine.getIssueDate()) : null);
            stmt.setDate(5, fine.getDueDate() != null ? Date.valueOf(fine.getDueDate()) : null);
            stmt.setString(6, fine.getStatus().name());
            stmt.setTimestamp(7, Timestamp.valueOf(fine.getUpdatedAt()));
            stmt.setLong(8, fine.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Updating fine failed, no rows affected.");
            }

            return fine;

        } catch (SQLException e) {
            logger.error("Error updating fine", e);
            throw new DAOException("Failed to update fine", e);
        }
    }

    @Override
    public void delete(Fine fine) throws DAOException {
        deleteById(fine.getId());
    }

    @Override
    public void deleteById(Long id) throws DAOException {
        String sql = "DELETE FROM fines WHERE id = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Deleting fine failed, no rows affected.");
            }

        } catch (SQLException e) {
            logger.error("Error deleting fine with id {}", id, e);
            throw new DAOException("Failed to delete fine", e);
        }
    }

    @Override
    public List<Fine> findOverdueFines(LocalDate date) {
        String sql = "SELECT * FROM fines WHERE due_date < ? AND status = 'PENDING'";
        List<Fine> fines = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    fines.add(mapResultSetToFine(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding overdue fines for date {}", date, e);
        }

        return fines;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM fines";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            logger.error("Error counting fines", e);
        }

        return 0;
    }

    @Override
    public List<Fine> findByReason(Fine.FineReason reason) {
        String sql = "SELECT * FROM fines WHERE reason = ?";
        List<Fine> fines = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, reason.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    fines.add(mapResultSetToFine(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding fines by reason {}", reason, e);
        }

        return fines;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM fines WHERE id = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            logger.error("Error checking if fine exists with id {}", id, e);
            return false;
        }
    }

    @Override
    public List<Fine> findAll() {
        String sql = "SELECT * FROM fines ORDER BY id";
        List<Fine> fines = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                fines.add(mapResultSetToFine(rs));
            }

        } catch (SQLException e) {
            logger.error("Error finding all fines", e);
        }

        return fines;
    }

    @Override
    public List<Fine> findByStatus(Fine.FineStatus status) {
        String sql = "SELECT * FROM fines WHERE status = ?";
        List<Fine> fines = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    fines.add(mapResultSetToFine(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding fines by status {}", status, e);
        }

        return fines;
    }

    @Override
    public void clearAll() throws DAOException {
        String sql = "DELETE FROM fines";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Error clearing all fines", e);
            throw new DAOException("Failed to clear all fines", e);
        }
    }

    private Fine mapResultSetToFine(ResultSet rs) throws SQLException {
        Fine fine = new Fine();
        fine.setId(rs.getLong("id"));

        // Create stub Member object with just ID
        // In a full implementation, you might want to load this via MemberDAO
        Member member = new Member();
        member.setId(rs.getLong("member_id"));
        fine.setMember(member);

        fine.setAmount(rs.getBigDecimal("amount").doubleValue());
        fine.setReason(Fine.FineReason.valueOf(rs.getString("reason")));
        fine.setStatus(Fine.FineStatus.valueOf(rs.getString("status")));

        Date issueDate = rs.getDate("issue_date");
        if (issueDate != null) {
            fine.setIssueDate(issueDate.toLocalDate());
        }

        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) {
            fine.setDueDate(dueDate.toLocalDate());
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            fine.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            fine.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return fine;
    }
}