package com.davon.library.dao.impl;

import com.davon.library.dao.BookDAO;
import com.davon.library.dao.DAOException;
import com.davon.library.model.Book;
import com.davon.library.model.Author;
import com.davon.library.model.Category;
import com.davon.library.model.Publisher;

import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory implementation of BookDAO.
 * This implementation follows SOLID principles by separating data access
 * concerns.
 */
@ApplicationScoped
public class InMemoryBookDAOImpl extends AbstractInMemoryDAO<Book> implements BookDAO {

    @Override
    protected String getEntityName() {
        return "Book";
    }

    @Override
    protected Book cloneEntity(Book book) {
        if (book == null)
            return null;

        return Book.builder()
                .id(book.getId())
                .title(book.getTitle())
                .ISBN(book.getISBN())
                .publicationYear(book.getPublicationYear())
                .description(book.getDescription())
                .coverImage(book.getCoverImage())
                .pages(book.getPages())
                .authors(book.getAuthors() != null ? book.getAuthors().stream().collect(Collectors.toSet()) : null)
                .publisher(book.getPublisher())
                .category(book.getCategory())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .build();
    }

    @Override
    protected void validateEntity(Book book) throws DAOException {
        super.validateEntity(book);

        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            throw new DAOException("Book title cannot be null or empty", "validate", getEntityName());
        }

        if (book.getISBN() == null || book.getISBN().trim().isEmpty()) {
            throw new DAOException("Book ISBN cannot be null or empty", "validate", getEntityName());
        }

        if (!book.validateISBN()) {
            throw new DAOException("Invalid ISBN format", "validate", getEntityName());
        }

        // Check for duplicate ISBN (excluding the current book when updating)
        Optional<Book> existingBook = findByISBN(book.getISBN());
        if (existingBook.isPresent() && !existingBook.get().getId().equals(book.getId())) {
            throw new DAOException("Book with ISBN " + book.getISBN() + " already exists", "validate", getEntityName());
        }
    }

    @Override
    public Optional<Book> findByISBN(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return Optional.empty();
        }

        return storage.values().stream()
                .filter(book -> isbn.equals(book.getISBN()))
                .map(this::cloneEntity)
                .findFirst();
    }

    @Override
    public List<Book> findByTitleContaining(String title) {
        if (title == null || title.trim().isEmpty()) {
            return List.of();
        }

        String lowerTitle = title.toLowerCase();
        return storage.values().stream()
                .filter(book -> book.getTitle() != null &&
                        book.getTitle().toLowerCase().contains(lowerTitle))
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findByAuthor(Author author) {
        if (author == null) {
            return List.of();
        }

        return storage.values().stream()
                .filter(book -> book.getAuthors() != null &&
                        book.getAuthors().contains(author))
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findByCategory(Category category) {
        if (category == null) {
            return List.of();
        }

        return storage.values().stream()
                .filter(book -> category.equals(book.getCategory()))
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findByPublisher(Publisher publisher) {
        if (publisher == null) {
            return List.of();
        }

        return storage.values().stream()
                .filter(book -> publisher.equals(book.getPublisher()))
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findByPublicationYear(int year) {
        return storage.values().stream()
                .filter(book -> book.getPublicationYear() == year)
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findByPublicationYearBetween(int startYear, int endYear) {
        return storage.values().stream()
                .filter(book -> book.getPublicationYear() >= startYear &&
                        book.getPublicationYear() <= endYear)
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> searchBooks(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return List.of();
        }

        String lowerSearchTerm = searchTerm.toLowerCase();
        return storage.values().stream()
                .filter(book -> matchesSearchTerm(book, lowerSearchTerm))
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> findAvailableBooks() {
        return storage.values().stream()
                .filter(Book::isAvailable)
                .map(this::cloneEntity)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByISBN(String isbn) {
        return findByISBN(isbn).isPresent();
    }

    /**
     * Helper method to check if a book matches the search term.
     * 
     * @param book       the book to check
     * @param searchTerm the search term (already lowercased)
     * @return true if the book matches the search term
     */
    private boolean matchesSearchTerm(Book book, String searchTerm) {
        return (book.getTitle() != null && book.getTitle().toLowerCase().contains(searchTerm)) ||
                (book.getISBN() != null && book.getISBN().toLowerCase().contains(searchTerm)) ||
                (book.getDescription() != null && book.getDescription().toLowerCase().contains(searchTerm)) ||
                (book.getAuthors() != null && book.getAuthors().stream()
                        .anyMatch(author -> author.getName() != null &&
                                author.getName().toLowerCase().contains(searchTerm)));
    }
}