package com.davon.library.dao.impl;

import com.davon.library.dao.BookCopyDAO;
import com.davon.library.dao.DAOException;
import com.davon.library.database.DatabaseConnectionManager;
import com.davon.library.model.Book;
import com.davon.library.model.BookCopy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class MSSQLBookCopyDAOImpl implements BookCopyDAO {
    private static final Logger logger = LoggerFactory.getLogger(MSSQLBookCopyDAOImpl.class);

    @Inject
    DatabaseConnectionManager connectionManager;

    @Override
    public BookCopy save(BookCopy bookCopy) throws DAOException {
        String sql = "INSERT INTO book_copies (book_id, location, status, condition, acquisition_date, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            LocalDateTime now = LocalDateTime.now();
            bookCopy.setCreatedAt(now);
            bookCopy.setUpdatedAt(now);

            stmt.setLong(1, bookCopy.getBook() != null ? bookCopy.getBook().getId() : null);
            stmt.setString(2, bookCopy.getLocation());
            stmt.setString(3, bookCopy.getStatus().name());
            stmt.setString(4, bookCopy.getCondition());
            stmt.setDate(5, bookCopy.getAcquisitionDate() != null ? Date.valueOf(bookCopy.getAcquisitionDate()) : null);
            stmt.setTimestamp(6, Timestamp.valueOf(now));
            stmt.setTimestamp(7, Timestamp.valueOf(now));

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Creating book copy failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    bookCopy.setId(generatedKeys.getLong(1));
                }
            }

            return bookCopy;

        } catch (SQLException e) {
            logger.error("Error saving book copy", e);
            throw new DAOException("Failed to save book copy", e);
        }
    }

    @Override
    public BookCopy update(BookCopy bookCopy) throws DAOException {
        String sql = "UPDATE book_copies SET book_id = ?, location = ?, status = ?, condition = ?, acquisition_date = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            bookCopy.setUpdatedAt(LocalDateTime.now());

            stmt.setLong(1, bookCopy.getBook() != null ? bookCopy.getBook().getId() : null);
            stmt.setString(2, bookCopy.getLocation());
            stmt.setString(3, bookCopy.getStatus().name());
            stmt.setString(4, bookCopy.getCondition());
            stmt.setDate(5, bookCopy.getAcquisitionDate() != null ? Date.valueOf(bookCopy.getAcquisitionDate()) : null);
            stmt.setTimestamp(6, Timestamp.valueOf(bookCopy.getUpdatedAt()));
            stmt.setLong(7, bookCopy.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Updating book copy failed, no rows affected.");
            }

            return bookCopy;

        } catch (SQLException e) {
            logger.error("Error updating book copy", e);
            throw new DAOException("Failed to update book copy", e);
        }
    }

    @Override
    public void delete(BookCopy bookCopy) throws DAOException {
        deleteById(bookCopy.getId());
    }

    @Override
    public void deleteById(Long id) throws DAOException {
        String sql = "DELETE FROM book_copies WHERE id = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Deleting book copy failed, no rows affected.");
            }

        } catch (SQLException e) {
            logger.error("Error deleting book copy with id {}", id, e);
            throw new DAOException("Failed to delete book copy", e);
        }
    }

    @Override
    public Optional<BookCopy> findById(Long id) {
        String sql = "SELECT * FROM book_copies WHERE id = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBookCopy(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding book copy with id {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<BookCopy> findAll() {
        String sql = "SELECT * FROM book_copies ORDER BY id";
        List<BookCopy> bookCopies = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                bookCopies.add(mapResultSetToBookCopy(rs));
            }

        } catch (SQLException e) {
            logger.error("Error finding all book copies", e);
        }

        return bookCopies;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM book_copies WHERE id = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            logger.error("Error checking if book copy exists with id {}", id, e);
            return false;
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM book_copies";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            logger.error("Error counting book copies", e);
        }

        return 0;
    }

    @Override
    public List<BookCopy> findByBook(Book book) {
        String sql = "SELECT * FROM book_copies WHERE book_id = ?";
        List<BookCopy> bookCopies = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, book.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookCopies.add(mapResultSetToBookCopy(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding book copies by book {}", book.getId(), e);
        }

        return bookCopies;
    }

    @Override
    public List<BookCopy> findAvailableByBook(Book book) {
        String sql = "SELECT * FROM book_copies WHERE book_id = ? AND status = 'AVAILABLE'";
        List<BookCopy> bookCopies = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, book.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookCopies.add(mapResultSetToBookCopy(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding available book copies by book {}", book.getId(), e);
        }

        return bookCopies;
    }

    @Override
    public List<BookCopy> findByStatus(BookCopy.CopyStatus status) {
        String sql = "SELECT * FROM book_copies WHERE status = ?";
        List<BookCopy> bookCopies = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookCopies.add(mapResultSetToBookCopy(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding book copies by status {}", status, e);
        }

        return bookCopies;
    }

    @Override
    public Optional<BookCopy> findByBarcode(String barcode) {
        // Note: BookCopy model doesn't have barcode field in this implementation
        // This method returns empty for now - consider adding barcode field to model if
        // needed
        logger.warn("findByBarcode called but BookCopy model doesn't have barcode field");
        return Optional.empty();
    }

    @Override
    public long countAvailableByBook(Book book) {
        String sql = "SELECT COUNT(*) FROM book_copies WHERE book_id = ? AND status = 'AVAILABLE'";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, book.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }

        } catch (SQLException e) {
            logger.error("Error counting available book copies by book {}", book.getId(), e);
        }

        return 0;
    }

    @Override
    public List<BookCopy> findByLocation(String location) {
        String sql = "SELECT * FROM book_copies WHERE location = ?";
        List<BookCopy> bookCopies = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, location);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookCopies.add(mapResultSetToBookCopy(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding book copies by location {}", location, e);
        }

        return bookCopies;
    }

    @Override
    public void clearAll() throws DAOException {
        String sql = "DELETE FROM book_copies";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Error clearing all book copies", e);
            throw new DAOException("Failed to clear all book copies", e);
        }
    }

    private BookCopy mapResultSetToBookCopy(ResultSet rs) throws SQLException {
        BookCopy bookCopy = new BookCopy();
        bookCopy.setId(rs.getLong("id"));

        // Create stub Book object with just ID
        // In a full implementation, you might want to load this via BookDAO
        Book book = new Book();
        book.setId(rs.getLong("book_id"));
        bookCopy.setBook(book);

        bookCopy.setLocation(rs.getString("location"));
        bookCopy.setStatus(BookCopy.CopyStatus.valueOf(rs.getString("status")));
        bookCopy.setCondition(rs.getString("condition"));

        Date acquisitionDate = rs.getDate("acquisition_date");
        if (acquisitionDate != null) {
            bookCopy.setAcquisitionDate(acquisitionDate.toLocalDate());
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            bookCopy.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            bookCopy.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return bookCopy;
    }
}