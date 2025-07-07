package com.davon.library.dao.impl;

import com.davon.library.dao.BookDAO;
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
 * MSSQL implementation of BookDAO.
 */
@ApplicationScoped
public class MSSQLBookDAOImpl implements BookDAO {
    private static final Logger logger = LoggerFactory.getLogger(MSSQLBookDAOImpl.class);

    @Inject
    DatabaseConnectionManager connectionManager;

    @Override
    public Book save(Book entity) throws DAOException {
        String sql = "INSERT INTO books (title, isbn, publication_year, description, cover_image, pages, publisher_id, category_id, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            LocalDateTime now = LocalDateTime.now();
            entity.setCreatedAt(now);
            entity.setUpdatedAt(now);

            stmt.setString(1, entity.getTitle());
            stmt.setString(2, entity.getISBN());
            if (entity.getPublicationYear() > 0) {
                stmt.setInt(3, entity.getPublicationYear());
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            stmt.setString(4, entity.getDescription());
            stmt.setString(5, entity.getCoverImage());
            if (entity.getPages() > 0) {
                stmt.setInt(6, entity.getPages());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }
            // Handle foreign keys - set to null for now since Publisher and Category
            // objects may not be fully implemented
            if (entity.getPublisher() != null && entity.getPublisher().getId() != null) {
                stmt.setLong(7, entity.getPublisher().getId());
            } else {
                stmt.setNull(7, java.sql.Types.BIGINT);
            }
            if (entity.getCategory() != null && entity.getCategory().getId() != null) {
                stmt.setLong(8, entity.getCategory().getId());
            } else {
                stmt.setNull(8, java.sql.Types.BIGINT);
            }
            stmt.setTimestamp(9, Timestamp.valueOf(now));
            stmt.setTimestamp(10, Timestamp.valueOf(now));

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Creating book failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
            }

            return entity;

        } catch (SQLException e) {
            logger.error("Error saving book", e);
            throw new DAOException("Failed to save book", e);
        }
    }

    @Override
    public Book update(Book entity) throws DAOException {
        String sql = "UPDATE books SET title = ?, isbn = ?, publication_year = ?, description = ?, cover_image = ?, pages = ?, publisher_id = ?, category_id = ?, updated_at = ? WHERE id = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            entity.setUpdatedAt(LocalDateTime.now());

            stmt.setString(1, entity.getTitle());
            stmt.setString(2, entity.getISBN());
            if (entity.getPublicationYear() > 0) {
                stmt.setInt(3, entity.getPublicationYear());
            } else {
                stmt.setNull(3, java.sql.Types.INTEGER);
            }
            stmt.setString(4, entity.getDescription());
            stmt.setString(5, entity.getCoverImage());
            if (entity.getPages() > 0) {
                stmt.setInt(6, entity.getPages());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }
            // Handle foreign keys
            if (entity.getPublisher() != null && entity.getPublisher().getId() != null) {
                stmt.setLong(7, entity.getPublisher().getId());
            } else {
                stmt.setNull(7, java.sql.Types.BIGINT);
            }
            if (entity.getCategory() != null && entity.getCategory().getId() != null) {
                stmt.setLong(8, entity.getCategory().getId());
            } else {
                stmt.setNull(8, java.sql.Types.BIGINT);
            }
            stmt.setTimestamp(9, Timestamp.valueOf(entity.getUpdatedAt()));
            stmt.setLong(10, entity.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Updating book failed, no rows affected.");
            }

            return entity;

        } catch (SQLException e) {
            logger.error("Error updating book", e);
            throw new DAOException("Failed to update book", e);
        }
    }

    @Override
    public void delete(Book entity) throws DAOException {
        deleteById(entity.getId());
    }

    @Override
    public void deleteById(Long id) throws DAOException {
        String sql = "DELETE FROM books WHERE id = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new DAOException("Deleting book failed, no rows affected.");
            }

        } catch (SQLException e) {
            logger.error("Error deleting book with id {}", id, e);
            throw new DAOException("Failed to delete book", e);
        }
    }

    @Override
    public Optional<Book> findById(Long id) {
        String sql = "SELECT * FROM books WHERE id = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBook(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding book with id {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<Book> findAll() {
        String sql = "SELECT * FROM books ORDER BY id";
        List<Book> books = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }

        } catch (SQLException e) {
            logger.error("Error finding all books", e);
        }

        return books;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM books WHERE id = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            logger.error("Error checking if book exists with id {}", id, e);
            return false;
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM books";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getLong(1);
            }

        } catch (SQLException e) {
            logger.error("Error counting books", e);
        }

        return 0;
    }

    @Override
    public Optional<Book> findByISBN(String isbn) {
        String sql = "SELECT * FROM books WHERE isbn = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, isbn);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToBook(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            logger.error("Error finding book by ISBN {}", isbn, e);
            return Optional.empty();
        }
    }

    @Override
    public List<Book> findByTitleContaining(String title) {
        String sql = "SELECT * FROM books WHERE title LIKE ?";
        List<Book> books = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + title + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding books by title {}", title, e);
        }

        return books;
    }

    @Override
    public List<Book> searchBooks(String searchTerm) {
        String sql = "SELECT * FROM books WHERE title LIKE ? OR isbn LIKE ? OR description LIKE ?";
        List<Book> books = new ArrayList<>();
        String likePattern = "%" + searchTerm + "%";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, likePattern);
            stmt.setString(2, likePattern);
            stmt.setString(3, likePattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error searching books with term {}", searchTerm, e);
        }

        return books;
    }

    @Override
    public List<Book> findByAuthor(Author author) {
        // This would require a join with book_authors table
        // For now, return empty list as implementation is complex
        logger.warn("findByAuthor not yet implemented");
        return new ArrayList<>();
    }

    @Override
    public List<Book> findByCategory(Category category) {
        String sql = "SELECT * FROM books WHERE category_id = ?";
        List<Book> books = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, category.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding books by category {}", category.getId(), e);
        }

        return books;
    }

    @Override
    public List<Book> findByPublisher(Publisher publisher) {
        String sql = "SELECT * FROM books WHERE publisher_id = ?";
        List<Book> books = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, publisher.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding books by publisher {}", publisher.getId(), e);
        }

        return books;
    }

    @Override
    public List<Book> findByPublicationYear(int year) {
        String sql = "SELECT * FROM books WHERE publication_year = ?";
        List<Book> books = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, year);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding books by publication year {}", year, e);
        }

        return books;
    }

    @Override
    public List<Book> findByPublicationYearBetween(int startYear, int endYear) {
        String sql = "SELECT * FROM books WHERE publication_year BETWEEN ? AND ?";
        List<Book> books = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, startYear);
            stmt.setInt(2, endYear);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    books.add(mapResultSetToBook(rs));
                }
            }

        } catch (SQLException e) {
            logger.error("Error finding books by publication year between {} and {}", startYear, endYear, e);
        }

        return books;
    }

    @Override
    public List<Book> findAvailableBooks() {
        String sql = """
                SELECT DISTINCT b.* FROM books b
                JOIN book_copies bc ON b.id = bc.book_id
                WHERE bc.status = 'AVAILABLE'
                """;
        List<Book> books = new ArrayList<>();

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }

        } catch (SQLException e) {
            logger.error("Error finding available books", e);
        }

        return books;
    }

    @Override
    public boolean existsByISBN(String isbn) {
        String sql = "SELECT 1 FROM books WHERE isbn = ?";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, isbn);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            logger.error("Error checking if book exists with ISBN {}", isbn, e);
            return false;
        }
    }

    @Override
    public void clearAll() throws DAOException {
        String sql = "DELETE FROM books";

        try (Connection conn = connectionManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("Error clearing all books", e);
            throw new DAOException("Failed to clear all books", e);
        }
    }

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        return Book.builder()
                .id(rs.getLong("id"))
                .title(rs.getString("title"))
                .ISBN(rs.getString("isbn"))
                .publicationYear(rs.getInt("publication_year"))
                .description(rs.getString("description"))
                .coverImage(rs.getString("cover_image"))
                .pages(rs.getInt("pages"))
                // Handle foreign keys - for now just set objects to null since we don't have
                // full publisher/category lookup implemented
                .publisher(rs.getLong("publisher_id") != 0 ? Publisher.builder().id(rs.getLong("publisher_id")).build()
                        : null)
                .category(rs.getLong("category_id") != 0 ? Category.builder().id(rs.getLong("category_id")).build()
                        : null)
                .createdAt(
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null)
                .updatedAt(
                        rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null)
                .build();
    }
}
