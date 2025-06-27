package com.davon.library.dao;

import com.davon.library.model.Book;
import com.davon.library.model.Author;
import com.davon.library.model.Category;
import com.davon.library.model.Publisher;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Object interface for Book entities.
 * Extends BaseDAO with book-specific query methods.
 */
public interface BookDAO extends BaseDAO<Book, Long> {

    /**
     * Finds a book by its ISBN.
     * 
     * @param isbn the ISBN to search for
     * @return an Optional containing the book if found, empty otherwise
     */
    Optional<Book> findByISBN(String isbn);

    /**
     * Finds books by title (case-insensitive partial match).
     * 
     * @param title the title to search for
     * @return a list of books matching the title
     */
    List<Book> findByTitleContaining(String title);

    /**
     * Finds books by author.
     * 
     * @param author the author to search for
     * @return a list of books by the specified author
     */
    List<Book> findByAuthor(Author author);

    /**
     * Finds books by category.
     * 
     * @param category the category to search for
     * @return a list of books in the specified category
     */
    List<Book> findByCategory(Category category);

    /**
     * Finds books by publisher.
     * 
     * @param publisher the publisher to search for
     * @return a list of books from the specified publisher
     */
    List<Book> findByPublisher(Publisher publisher);

    /**
     * Finds books published in a specific year.
     * 
     * @param year the publication year
     * @return a list of books published in the specified year
     */
    List<Book> findByPublicationYear(int year);

    /**
     * Finds books published between two years (inclusive).
     * 
     * @param startYear the start year (inclusive)
     * @param endYear   the end year (inclusive)
     * @return a list of books published in the specified year range
     */
    List<Book> findByPublicationYearBetween(int startYear, int endYear);

    /**
     * Searches books by multiple criteria (title, ISBN, description).
     * 
     * @param searchTerm the search term to look for
     * @return a list of books matching the search criteria
     */
    List<Book> searchBooks(String searchTerm);

    /**
     * Finds available books (books with available copies).
     * 
     * @return a list of available books
     */
    List<Book> findAvailableBooks();

    /**
     * Checks if a book with the given ISBN already exists.
     * 
     * @param isbn the ISBN to check
     * @return true if a book with this ISBN exists, false otherwise
     */
    boolean existsByISBN(String isbn);
}