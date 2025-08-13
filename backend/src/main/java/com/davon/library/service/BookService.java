package com.davon.library.service;

import com.davon.library.model.Book;
import com.davon.library.model.Category;
import com.davon.library.repository.BookRepository;
import com.davon.library.repository.BookCopyRepository;
import com.davon.library.repository.CategoryRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.davon.library.dto.BookRequestDTO;
import com.davon.library.mapper.BookMapper;
import com.davon.library.model.Author;
import com.davon.library.model.Publisher;
import com.davon.library.repository.AuthorRepository;
import com.davon.library.repository.PublisherRepository;

@ApplicationScoped
public class BookService {

    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    @Inject
    BookRepository bookRepository;

    @Inject
    BookCopyRepository bookCopyRepository;

    @Inject
    CategoryRepository categoryRepository;

    @Inject
    AuthorRepository authorRepository;

    @Inject
    PublisherRepository publisherRepository;

    @Transactional
    public Book createBook(BookRequestDTO bookRequestDTO) {
        log.debug("Creating a new book with title: {}", bookRequestDTO.title());

        Book book = BookMapper.toEntity(bookRequestDTO);

        Publisher publisher = publisherRepository.findByIdOptional(bookRequestDTO.publisherId())
                .orElseThrow(() -> new NotFoundException("Publisher not found with ID: " + bookRequestDTO.publisherId()));
        book.setPublisher(publisher);

        Category category = categoryRepository.findByIdOptional(bookRequestDTO.categoryId())
                .orElseThrow(() -> new NotFoundException("Category not found with ID: " + bookRequestDTO.categoryId()));
        book.setCategory(category);

        if (bookRequestDTO.authorIds() != null && !bookRequestDTO.authorIds().isEmpty()) {
            Set<Author> authors = bookRequestDTO.authorIds().stream()
                    .map(authorId -> authorRepository.findByIdOptional(authorId)
                            .orElseThrow(() -> new NotFoundException("Author not found with ID: " + authorId)))
                    .collect(Collectors.toSet());
            book.setAuthors(authors);
        }

        bookRepository.persist(book);
        log.info("Successfully created book with ID: {}", book.getId());
        return book;
    }

    @Transactional
    public Book updateBook(Long bookId, Book updatedBook) {
        log.debug("Updating book: {}", bookId);
        Book existingBook = bookRepository.findByIdOptional(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + bookId));

        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setDescription(updatedBook.getDescription());
        existingBook.setPublicationYear(updatedBook.getPublicationYear());
        existingBook.setPublisher(updatedBook.getPublisher());
        existingBook.setCategory(updatedBook.getCategory());
        existingBook.setAuthors(updatedBook.getAuthors());

        return existingBook;
    }

    @Transactional
    public void deleteBook(Long bookId) {
        log.debug("Deleting book: {}", bookId);
        Book book = bookRepository.findByIdOptional(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found with ID: " + bookId));
        
        bookCopyRepository.delete("book.id", book.getId());
        bookRepository.delete(book);
    }

    public List<Book> getAllBooks() {
        log.debug("Fetching all books");
        return bookRepository.listAll();
    }

    public Optional<Book> getBookById(Long bookId) {
        log.debug("Fetching book by ID: {}", bookId);
        return bookRepository.findByIdOptional(bookId);
    }

    public List<Book> searchBooks(String query) {
        log.debug("Searching books with query: {}", query);
        return bookRepository.search(query);
    }

    public boolean isBookAvailable(Long bookId) {
        return bookCopyRepository.count("book.id = ?1 and status = 'AVAILABLE'", bookId) > 0;
    }

    public List<Book> getBooksByCategory(Long categoryId) {
        Category category = categoryRepository.findByIdOptional(categoryId)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        return bookRepository.findByCategory(category);
    }
}
