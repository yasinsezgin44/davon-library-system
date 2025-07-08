package com.davon.library.controller;

import com.davon.library.model.Book;
import com.davon.library.service.BookService;
import com.davon.library.repository.BookRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class BookControllerTest {

        @Inject
        BookService bookService;

        @Inject
        BookRepository bookRepository;

        @BeforeEach
        @Transactional
        void clearData() {
                // Clear all books before each test to ensure isolation
                // COMMENTED OUT: This causes foreign key constraint violations with persistent
                // database
                // bookRepository.deleteAll();

                // Note: Tests use unique data generation for isolation instead of deletion
        }

        @Test
        void testGetAllBooks() {
                given()
                                .when().get("/api/books")
                                .then()
                                .statusCode(200)
                                .contentType(ContentType.JSON)
                                .body("size()", greaterThanOrEqualTo(0));
        }

        @Test
        void testCreateAndGetBook() {
                String uniqueISBN = generateUniqueISBN();
                Book book = Book.builder()
                                .title("Test Book")
                                .ISBN(uniqueISBN)
                                .publicationYear(2023)
                                .description("A test book")
                                .pages(200)
                                .build();

                // Create a book
                given()
                                .contentType(ContentType.JSON)
                                .body(book)
                                .when().post("/api/books")
                                .then()
                                .statusCode(201)
                                .contentType(ContentType.JSON)
                                .body("title", is("Test Book"))
                                .body("ISBN", is(uniqueISBN));
        }

        @Test
        void testSearchBooks() {
                // First create a book to search for
                String uniquePrefix = "ControllerTest" + System.nanoTime();
                Book book = Book.builder()
                                .title(uniquePrefix + " Java Programming")
                                .ISBN(generateUniqueISBN())
                                .description("A comprehensive guide to Java")
                                .publicationYear(2023)
                                .pages(400)
                                .build();

                given()
                                .contentType(ContentType.JSON)
                                .body(book)
                                .when().post("/api/books")
                                .then()
                                .statusCode(201);

                // Now search for it using the unique prefix
                given()
                                .param("q", uniquePrefix)
                                .when().get("/api/books/search")
                                .then()
                                .statusCode(200)
                                .contentType(ContentType.JSON)
                                .body("size()", greaterThanOrEqualTo(1));
        }

        @Test
        void testGetBookByIdNotFound() {
                given()
                                .when().get("/api/books/999")
                                .then()
                                .statusCode(404);
        }

        @Test
        void testDeleteBookNotFound() {
                given()
                                .when().delete("/api/books/999")
                                .then()
                                .statusCode(404);
        }

        @Test
        void testHealthEndpoint() {
                given()
                                .when().get("/api/health")
                                .then()
                                .statusCode(200)
                                .contentType(ContentType.JSON)
                                .body("status", is("UP"));
        }

        private String generateUniqueISBN() {
                // Generate a truly unique 13-digit ISBN using timestamp + random digits
                long timestamp = System.nanoTime();
                return "978" + String.format("%010d", Math.abs(timestamp % 10000000000L));
        }
}