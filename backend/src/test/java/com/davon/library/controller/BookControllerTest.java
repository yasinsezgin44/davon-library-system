package com.davon.library.controller;

import com.davon.library.model.Book;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class BookControllerTest {

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
        Book book = new Book();
        book.setTitle("Test Book");
        book.setISBN("9781234567890");
        book.setPublicationYear(2023);
        book.setDescription("A test book");

        // Create a book
        given()
                .contentType(ContentType.JSON)
                .body(book)
                .when().post("/api/books")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("title", is("Test Book"))
                .body("isbn", is("9781234567890"));
    }

    @Test
    void testSearchBooks() {
        // First create a book to search for
        Book book = new Book();
        book.setTitle("Java Programming");
        book.setISBN("9781111111111");
        book.setDescription("A comprehensive guide to Java");

        given()
                .contentType(ContentType.JSON)
                .body(book)
                .when().post("/api/books")
                .then()
                .statusCode(201);

        // Now search for it
        given()
                .param("q", "Java")
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
}