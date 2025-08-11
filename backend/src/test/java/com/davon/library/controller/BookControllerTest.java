package com.davon.library.controller;

import com.davon.library.model.Author;
import com.davon.library.model.Book;
import com.davon.library.service.BookService;
import com.davon.library.service.CategoryService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@QuarkusTest
class BookControllerTest {

    @InjectMock
    BookService bookService;

    @InjectMock
    CategoryService categoryService;

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setIsbn("1234567890");

        Author author = new Author();
        author.setId(1L);
        author.setName("Test Author");
        book.setAuthors(Set.of(author));
    }

    @Test
    void testGetAllBooks() {
        when(bookService.getAllBooks()).thenReturn(List.of(book));
        given()
                .when().get("/api/books")
                .then()
                .statusCode(200);
    }

    @Test
    @TestSecurity(user = "adminUser", roles = { "ADMIN" })
    void testGetAllBooks_authorizedAdmin() {
        when(bookService.getAllBooks()).thenReturn(List.of(book));
        given()
                .when().get("/api/books")
                .then()
                .statusCode(200)
                .body("$.size()", is(1))
                .body("[0].title", is("Test Book"));
    }

    @Test
    @TestSecurity(user = "memberUser", roles = { "MEMBER" })
    void testGetAllBooks_forbiddenNonAdmin() {
        when(bookService.getAllBooks()).thenReturn(List.of(book));
        given()
                .when().get("/api/books")
                .then()
                .statusCode(200);
    }

    @Test
    @TestSecurity(user = "testUser", roles = { "ADMIN" })
    void testCreateBook_authorized() {
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
    void testCreateBook_unauthorized() {
        given()
                .contentType(ContentType.JSON)
                .body(book)
                .when().post("/api/books")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "testUser", roles = { "MEMBER" })
    void testCreateBook_forbidden() {
        given()
                .contentType(ContentType.JSON)
                .body(book)
                .when().post("/api/books")
                .then()
                .statusCode(403);
    }

    @Test
    void testSearchBooks_public() {
        when(bookService.searchBooks("abc")).thenReturn(List.of(book));
        given()
                .when().get("/api/books/search?query=abc")
                .then()
                .statusCode(200)
                .body("$.size()", is(1));
    }

    @Test
    void testSearchBooks_byAuthor() {
        when(bookService.searchBooks(anyString())).thenReturn(List.of(book));
        given()
                .when().get("/api/books/search?query=Test Author")
                .then()
                .statusCode(200)
                .body("$.size()", is(1))
                .body("[0].title", is("Test Book"));
    }

    @Test
    void testTrendingBooks_public() {
        when(bookService.getAllBooks()).thenReturn(List.of(book));
        given()
                .when().get("/api/books/trending")
                .then()
                .statusCode(200)
                .body("$.size()", is(1));
    }

    @Test
    void testGenres_public() {
        when(categoryService.getAllCategories()).thenReturn(List.of());
        given()
                .when().get("/api/books/genres")
                .then()
                .statusCode(200);
    }
}
