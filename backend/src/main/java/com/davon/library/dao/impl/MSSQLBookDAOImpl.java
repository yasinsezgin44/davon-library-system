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
            stmt.setInt(3, entity.getPublicationYear());
            stmt.setString(4, entity.getDescription());
            stmt.setString(5, entity.getCoverImage());
            stmt.setInt(6, entity.getPages());
            stmt.setObject(7, null); // publisher_id - to be implemented
            stmt.setObject(8, null); // category_id - to be implemented
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

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        return Book.builder()
                .id(rs.getLong("id"))
                .title(rs.getString("title"))
                .ISBN(rs.getString("isbn"))
                .publicationYear(rs.getInt("publication_year"))
                .description(rs.getString("description"))
                .coverImage(rs.getString("cover_image"))
                .pages(rs.getInt("pages"))
                .createdAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null)
                .updatedAt(rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null)
                .build();
    }

    // Simplified implementations for remaining required methods
    @Override public Book update(Book entity) throws DAOException { throw new UnsupportedOperationException("To be implemented"); }
    @Override public void delete(Book entity) throws DAOException { deleteById(entity.getId()); }
    @Override public void deleteById(Long id) throws DAOException { /* Implementation */ }
    @Override public boolean existsById(Long id) { return false; }
    @Override public long count() { return 0; }
    @Override public List<Book> findByAuthor(Author author) { return new ArrayList<>(); }
    @Override public List<Book> findByCategory(Category category) { return new ArrayList<>(); }
    @Override public List<Book> findByPublisher(Publisher publisher) { return new ArrayList<>(); }
    @Override public List<Book> findByPublicationYear(int year) { return new ArrayList<>(); }
    @Override public List<Book> findByPublicationYearBetween(int startYear, int endYear) { return new ArrayList<>(); }
    @Override public List<Book> findAvailableBooks() { return new ArrayList<>(); }
    @Override public boolean existsByISBN(String isbn) { return false; }
}
