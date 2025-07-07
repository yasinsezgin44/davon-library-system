package com.davon.library.dao.impl;

import com.davon.library.dao.LoanDAO;
import com.davon.library.dao.DAOException;
import com.davon.library.database.DatabaseConnectionManager;
import com.davon.library.model.Loan;
import com.davon.library.model.Member;
import com.davon.library.model.BookCopy;
import com.davon.library.dao.BookCopyDAO;
import com.davon.library.dao.UserDAO;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MSSQLLoanDAOImpl implements LoanDAO {
    private static final Logger logger = LoggerFactory.getLogger(MSSQLLoanDAOImpl.class);

    @Inject
    DatabaseConnectionManager connectionManager;

    @Inject
    BookCopyDAO bookCopyDAO;

    @Inject
    UserDAO userDAO;

    @Override
    public Loan save(Loan loan) throws DAOException {
        String sql = "INSERT INTO loans (member_id, book_copy_id, checkout_date, due_date, status, renewal_count, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            LocalDateTime now = LocalDateTime.now();
            loan.setCreatedAt(now);
            loan.setUpdatedAt(now);

            stmt.setLong(1, loan.getMember() != null ? loan.getMember().getId() : null);
            stmt.setLong(2, loan.getBookCopy() != null ? loan.getBookCopy().getId() : null);
            stmt.setDate(3, Date.valueOf(loan.getCheckoutDate()));
            stmt.setDate(4, Date.valueOf(loan.getDueDate()));
            stmt.setString(5, loan.getStatus().name());
            stmt.setInt(6, loan.getRenewalCount());
            stmt.setTimestamp(7, Timestamp.valueOf(now));
            stmt.setTimestamp(8, Timestamp.valueOf(now));

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Creating loan failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    loan.setId(generatedKeys.getLong(1));
                }
            }

            return loan;

        } catch (SQLException e) {
            logger.error("Error saving loan", e);
            throw new DAOException("Failed to save loan", e);
        }
    }

    @Override
    public Loan update(Loan loan) throws DAOException {
        String sql = "UPDATE loans SET member_id = ?, book_copy_id = ?, checkout_date = ?, due_date = ?, return_date = ?, status = ?, renewal_count = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            loan.setUpdatedAt(LocalDateTime.now());

            stmt.setLong(1, loan.getMember() != null ? loan.getMember().getId() : null);
            stmt.setLong(2, loan.getBookCopy() != null ? loan.getBookCopy().getId() : null);
            stmt.setDate(3, Date.valueOf(loan.getCheckoutDate()));
            stmt.setDate(4, Date.valueOf(loan.getDueDate()));
            stmt.setDate(5, loan.getReturnDate() != null ? Date.valueOf(loan.getReturnDate()) : null);
            stmt.setString(6, loan.getStatus().name());
            stmt.setInt(7, loan.getRenewalCount());
            stmt.setTimestamp(8, Timestamp.valueOf(loan.getUpdatedAt()));
            stmt.setLong(9, loan.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Updating loan failed, no rows affected.");
            }

            return loan;

        } catch (SQLException e) {
            logger.error("Error updating loan", e);
            throw new DAOException("Failed to update loan", e);
        }
    }

    @Override
    public void delete(Loan loan) throws DAOException {
        deleteById(loan.getId());
    }

    @Override
    public void deleteById(Long id) throws DAOException {
        String sql = "DELETE FROM loans WHERE id = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Deleting loan failed, no rows affected.");
            }

        } catch (SQLException e) {
            logger.error("Error deleting loan with id {}", id, e);
            throw new DAOException("Failed to delete loan", e);
        }
    }

    @Override
    public Optional<Loan> findById(Long id) {
        String sql = "SELECT * FROM loans WHERE id = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLoan(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding loan with id {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<Loan> findAll() {
        String sql = "SELECT * FROM loans ORDER BY id";
        List<Loan> loans = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                loans.add(mapResultSetToLoan(rs));
            }

        } catch (SQLException e) {
            logger.error("Error finding all loans", e);
        }

        return loans;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM loans WHERE id = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            logger.error("Error checking if loan exists with id {}", id, e);
            return false;
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM loans";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            logger.error("Error counting loans", e);
        }

        return 0;
    }

    @Override
    public List<Loan> findByMember(Member member) {
        String sql = "SELECT * FROM loans WHERE member_id = ?";
        List<Loan> loans = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, member.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapResultSetToLoan(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding loans by member {}", member.getId(), e);
        }

        return loans;
    }

    @Override
    public List<Loan> findActiveLoansByMember(Member member) {
        String sql = "SELECT * FROM loans WHERE member_id = ? AND status IN ('ACTIVE', 'OVERDUE')";
        List<Loan> loans = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, member.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapResultSetToLoan(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding active loans by member {}", member.getId(), e);
        }

        return loans;
    }

    @Override
    public Optional<Loan> findByBookCopyAndMember(BookCopy bookCopy, Member member) {
        String sql = "SELECT * FROM loans WHERE book_copy_id = ? AND member_id = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, bookCopy.getId());
            stmt.setLong(2, member.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLoan(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding loan by book copy {} and member {}", bookCopy.getId(), member.getId(), e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Loan> findActiveByBookCopy(BookCopy bookCopy) {
        String sql = "SELECT * FROM loans WHERE book_copy_id = ? AND status IN ('ACTIVE', 'OVERDUE')";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, bookCopy.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToLoan(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding active loan by book copy {}", bookCopy.getId(), e);
            return Optional.empty();
        }
    }

    @Override
    public List<Loan> findOverdueLoans(LocalDate date) {
        String sql = "SELECT * FROM loans WHERE due_date < ? AND status = 'ACTIVE'";
        List<Loan> loans = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapResultSetToLoan(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding overdue loans for date {}", date, e);
        }

        return loans;
    }

    @Override
    public List<Loan> findLoansDueOn(LocalDate date) {
        String sql = "SELECT * FROM loans WHERE due_date = ? AND status = 'ACTIVE'";
        List<Loan> loans = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(date));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapResultSetToLoan(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding loans due on date {}", date, e);
        }

        return loans;
    }

    @Override
    public List<Loan> findByStatus(Loan.LoanStatus status) {
        String sql = "SELECT * FROM loans WHERE status = ?";
        List<Loan> loans = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    loans.add(mapResultSetToLoan(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding loans by status {}", status, e);
        }

        return loans;
    }

    @Override
    public long countActiveLoansByMember(Member member) {
        String sql = "SELECT COUNT(*) FROM loans WHERE member_id = ? AND status IN ('ACTIVE', 'OVERDUE')";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, member.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }

        } catch (SQLException e) {
            logger.error("Error counting active loans by member {}", member.getId(), e);
        }

        return 0;
    }

    @Override
    public void clearAll() throws DAOException {
        String sql = "DELETE FROM loans";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Error clearing all loans", e);
            throw new DAOException("Failed to clear all loans", e);
        }
    }

    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setId(rs.getLong("id"));

        // Load the full Member object instead of creating a stub
        Long memberId = rs.getLong("member_id");
        try {
            Member member = (Member) userDAO.findById(memberId).orElse(null);
            if (member == null) {
                // Fallback to stub if not found
                member = new Member();
                member.setId(memberId);
                logger.warn("Member with ID {} not found, using stub", memberId);
            }
            loan.setMember(member);
        } catch (Exception e) {
            // Fallback to stub on error
            logger.warn("Error loading Member with ID {}, using stub: {}", memberId, e.getMessage());
            Member member = new Member();
            member.setId(memberId);
            loan.setMember(member);
        }

        // Load the full BookCopy object with its book reference
        Long bookCopyId = rs.getLong("book_copy_id");
        try {
            BookCopy bookCopy = bookCopyDAO.findById(bookCopyId).orElse(null);
            if (bookCopy == null) {
                // Fallback to stub if not found
                bookCopy = new BookCopy();
                bookCopy.setId(bookCopyId);
                logger.warn("BookCopy with ID {} not found, using stub", bookCopyId);
            }
            loan.setBookCopy(bookCopy);
        } catch (Exception e) {
            // Fallback to stub on error
            logger.warn("Error loading BookCopy with ID {}, using stub: {}", bookCopyId, e.getMessage());
            BookCopy bookCopy = new BookCopy();
            bookCopy.setId(bookCopyId);
            loan.setBookCopy(bookCopy);
        }

        loan.setCheckoutDate(rs.getDate("checkout_date").toLocalDate());
        loan.setDueDate(rs.getDate("due_date").toLocalDate());

        Date returnDate = rs.getDate("return_date");
        if (returnDate != null) {
            loan.setReturnDate(returnDate.toLocalDate());
        }

        loan.setStatus(Loan.LoanStatus.valueOf(rs.getString("status")));
        loan.setRenewalCount(rs.getInt("renewal_count"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            loan.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            loan.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return loan;
    }
}