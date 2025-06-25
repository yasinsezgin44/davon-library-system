package com.davon.library.controller;

import com.davon.library.model.Book;
import com.davon.library.service.BookService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@QuarkusTest
class BookControllerTest {

    @Inject
    BookController bookController;

    @InjectMock
    BookService bookService;

    @Test
    void testGetAllBooks() {
        // Arrange
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Book 1");

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Book 2");

        List<Book> books = Arrays.asList(book1, book2);

        when(bookService.getAllBooks()).thenReturn(books);

        // Act
        List<Book> result = bookController.getAllBooks();

        // Assert
        assertEquals(2, result.size());
        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    void testGetBookById() {
        // Arrange
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");

        when(bookService.getBookById(1L)).thenReturn(book);

        // Act
        Response response = bookController.getBookById(1L);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Book result = (Book) response.getEntity();
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Book", result.getTitle());
        verify(bookService, times(1)).getBookById(1L);
    }

    @Test
    void testGetBookById_NotFound() {
        // Arrange
        when(bookService.getBookById(1L)).thenThrow(new RuntimeException("Book not found"));

        // Act
        Response response = bookController.getBookById(1L);

        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(bookService, times(1)).getBookById(1L);
    }

    @Test
    void testCreateBook() {
        // Arrange
        Book book = new Book();
        book.setTitle("New Book");

        when(bookService.createBook(any(Book.class))).thenReturn(book);

        // Act
        Response response = bookController.createBook(book);

        // Assert
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Book result = (Book) response.getEntity();
        assertNotNull(result);
        assertEquals("New Book", result.getTitle());
        verify(bookService, times(1)).createBook(any(Book.class));
    }

    @Test
    void testCreateBook_BadRequest() {
        // Arrange
        Book book = new Book();
        book.setTitle("New Book");

        when(bookService.createBook(any(Book.class))).thenThrow(new RuntimeException("Invalid book"));

        // Act
        Response response = bookController.createBook(book);

        // Assert
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        verify(bookService, times(1)).createBook(any(Book.class));
    }

    @Test
    void testUpdateBook() {
        // Arrange
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Updated Book");

        when(bookService.updateBook(anyLong(), any(Book.class))).thenReturn(book);

        // Act
        Response response = bookController.updateBook(1L, book);

        // Assert
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Book result = (Book) response.getEntity();
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Updated Book", result.getTitle());
        verify(bookService, times(1)).updateBook(anyLong(), any(Book.class));
    }

    @Test
    void testUpdateBook_NotFound() {
        // Arrange
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Updated Book");

        when(bookService.updateBook(anyLong(), any(Book.class))).thenThrow(new RuntimeException("Book not found"));

        // Act
        Response response = bookController.updateBook(1L, book);

        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(bookService, times(1)).updateBook(anyLong(), any(Book.class));
    }

    @Test
    void testDeleteBook() {
        // Arrange
        doNothing().when(bookService).deleteBook(1L);

        // Act
        Response response = bookController.deleteBook(1L);

        // Assert
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(bookService, times(1)).deleteBook(1L);
    }

    @Test
    void testDeleteBook_NotFound() {
        // Arrange
        doThrow(new RuntimeException("Book not found")).when(bookService).deleteBook(1L);

        // Act
        Response response = bookController.deleteBook(1L);

        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        verify(bookService, times(1)).deleteBook(1L);
    }

    @Test
    void testSearchBooks() {
        // Arrange
        Book book = new Book();
        book.setTitle("Java Programming");

        List<Book> books = Arrays.asList(book);

        when(bookService.searchBooks(anyString())).thenReturn(books);

        // Act
        List<Book> result = bookController.searchBooks("Java");

        // Assert
        assertEquals(1, result.size());
        assertEquals("Java Programming", result.get(0).getTitle());
        verify(bookService, times(1)).searchBooks("Java");
    }
}