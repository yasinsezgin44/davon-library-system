package com.davon.library.controller;

import com.davon.library.model.Book;
import com.davon.library.service.BookService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@QuarkusTest
class BookControllerTest {

    @InjectMock
    BookService bookService;

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setIsbn("1234567890");
    }

    @Test
    void testGetAllBooks() {
        when(bookService.getAllBooks()).thenReturn(List.of(book));
        given()
                .when().get("/api/books")
                .then()
                .statusCode(200)
                .body("$.size()", is(1))
                .body("[0].title", is("Test Book"));
    }

    @Test
    void testGetBookById() {
        when(bookService.getBookById(1L)).thenReturn(Optional.of(book));
        given()
                .when().get("/api/books/1")
                .then()
                .statusCode(200)
                .body("title", is("Test Book"));
    }

    @Test
    void testGetBookById_notFound() {
        when(bookService.getBookById(1L)).thenReturn(Optional.empty());
        given()
                .when().get("/api/books/1")
                .then()
                .statusCode(404);
    }

    @Test
    void testCreateBook() {
        when(bookService.createBook(any(Book.class))).thenReturn(book);
        given()
                .contentType(ContentType.JSON)
                .body(book)
                .when().post("/api/books")
                .then()
                .statusCode(201)
                .body("title", is("Test Book"));
    }

    @Test
    void testGetBooksByGenre() {
        when(bookService.getBooksByCategory(1L)).thenReturn(List.of(book));
        given()
                .when().get("/api/books/genre/1")
                .then()
                .statusCode(200)
                .body("$.size()", is(1))
                .body("[0].title", is("Test Book"));
    }
}
