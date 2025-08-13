package com.davon.library.controller;

import com.davon.library.model.Book;
import com.davon.library.service.BookService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import com.davon.library.dto.BookRequestDTO;
import io.quarkus.test.security.TestSecurity;

@QuarkusTest
class BookControllerTest {

    @InjectMock
    BookService bookService;

    @Test
    @TestSecurity(user = "librarian", roles = { "LIBRARIAN" })
    void testCreateBookEndpoint() {
        BookRequestDTO bookRequestDTO = new BookRequestDTO("New Book", "1234567890123", 2023, "description", "cover.jpg", 100, 1L, 1L, Set.of(1L));

        given()
            .contentType("application/json")
            .body(bookRequestDTO)
        .when()
            .post("/api/books")
        .then()
            .statusCode(201);
    }

    @Test
    void testGetAllBooksEndpoint() {
        given()
                .when().get("/api/books")
                .then()
                .statusCode(200);
    }

    @Test
    void testGetBookByIdEndpoint_found() {
        Book book = new Book();
        book.setId(1L);
        when(bookService.getBookById(1L)).thenReturn(Optional.of(book));

        given()
                .when().get("/api/books/1")
                .then()
                .statusCode(200);
    }

    @Test
    void testGetBookByIdEndpoint_notFound() {
        when(bookService.getBookById(anyLong())).thenReturn(Optional.empty());

        given()
                .when().get("/api/books/1")
                .then()
                .statusCode(404);
    }
}
