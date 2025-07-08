package com.davon.library.repository;

import com.davon.library.model.Book;
import com.davon.library.model.Author;
import com.davon.library.model.Category;
import com.davon.library.model.Publisher;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Book entities using Hibernate ORM with Panache.
 * Replaces the old JDBC-based BookDAO implementation.
 */
@ApplicationScoped
public class BookRepository implements PanacheRepository<Book> {

    /**
     * Finds a book by its ISBN.
     * 
     * @param isbn the ISBN to search for
     * @return the book if found, empty otherwise
     */
    public Optional<Book> findByISBN(String isbn) {
        return find("ISBN", isbn).firstResultOptional();
    }

    /**
     * Finds books by title containing the given string (case-insensitive).
     * 
     * @param title the title substring to search for
     * @return list of books matching the title
     */
    public List<Book> findByTitleContaining(String title) {
        return find("LOWER(title) LIKE LOWER(?1)", "%" + title + "%").list();
    }

    /**
     * Searches books by title, author name, or ISBN.
     * 
     * @param searchTerm the search term
     * @return list of books matching the search criteria
     */
    public List<Book> searchBooks(String searchTerm) {
        String lowerSearchTerm = "%" + searchTerm.toLowerCase() + "%";
        return find("LOWER(title) LIKE ?1 OR LOWER(ISBN) LIKE ?1 OR " +
                "EXISTS (SELECT 1 FROM Author a WHERE a MEMBER OF authors AND LOWER(a.name) LIKE ?1)",
                lowerSearchTerm).list();
    }

    /**
     * Finds books by author.
     * 
     * @param author the author to search for
     * @return list of books by the author
     */
    public List<Book> findByAuthor(Author author) {
        return find("?1 MEMBER OF authors", author).list();
    }

    /**
     * Finds books by category.
     * 
     * @param category the category to search for
     * @return list of books in the category
     */
    public List<Book> findByCategory(Category category) {
        return find("category", category).list();
    }

    /**
     * Finds books by publisher.
     * 
     * @param publisher the publisher to search for
     * @return list of books by the publisher
     */
    public List<Book> findByPublisher(Publisher publisher) {
        return find("publisher", publisher).list();
    }

    /**
     * Finds books by publication year.
     * 
     * @param year the publication year
     * @return list of books published in the given year
     */
    public List<Book> findByPublicationYear(int year) {
        return find("publicationYear", year).list();
    }

    /**
     * Finds books published between two years (inclusive).
     * 
     * @param startYear the start year
     * @param endYear   the end year
     * @return list of books published in the year range
     */
    public List<Book> findByPublicationYearBetween(int startYear, int endYear) {
        return find("publicationYear >= ?1 AND publicationYear <= ?2", startYear, endYear).list();
    }

    /**
     * Finds books that have available copies.
     * 
     * @return list of available books
     */
    public List<Book> findAvailableBooks() {
        return find("EXISTS (SELECT 1 FROM BookCopy bc WHERE bc.book = this AND bc.status = 'AVAILABLE')").list();
    }

    /**
     * Checks if a book exists by its ISBN.
     * 
     * @param isbn the ISBN to check
     * @return true if a book with this ISBN exists, false otherwise
     */
    public boolean existsByISBN(String isbn) {
        return find("ISBN", isbn).count() > 0;
    }
}