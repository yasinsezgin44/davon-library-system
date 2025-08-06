package com.davon.library;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NewApplicationIntegrationTest {

    private static final String VALID_ISBN = "978-0-13-708107-3";
    private static String bookId;

    @Test
    @Order(1)
    public void testHealthCheckEndpoint() {
        given()
                .when().get("/q/health")
                .then()
                .statusCode(200)
                .body("status", is("UP"));
    }

    @Test
    @Order(2)
    public void testCreateBook_Success() {
        String newBookJson = String.format("""
            {
                "title": "The Clean Coder",
                "isbn": "%s",
                "publicationYear": 2011,
                "description": "A Code of Conduct for Professional Programmers",
                "pages": 256
            }
            """, VALID_ISBN);

        bookId = given()
                .contentType(ContentType.JSON)
                .body(newBookJson)
                .when().post("/api/books")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .extract()
                .jsonPath()
                .getString("id");
    }

    @Test
    @Order(3)
    public void testGetBookById_Found() {
        given()
                .when().get("/api/books/" + bookId)
                .then()
                .statusCode(200)
                .body("id", equalTo(Integer.parseInt(bookId)))
                .body("title", equalTo("The Clean Coder"))
                .body("isbn", equalTo(VALID_ISBN));
    }

    @Test
    @Order(4)
    public void testUpdateBook_Success() {
        String updatedBookJson = """
            {
                "title": "The Clean Coder: Updated Edition",
                "publicationYear": 2022
            }
            """;

        given()
                .contentType(ContentType.JSON)
                .body(updatedBookJson)
                .when().put("/api/books/" + bookId)
                .then()
                .statusCode(200)
                .body("title", equalTo("The Clean Coder: Updated Edition"))
                .body("publicationYear", equalTo(2022));
    }

    @Test
    @Order(5)
    public void testDeleteBook_Success() {
        given()
                .when().delete("/api/books/" + bookId)
                .then()
                .statusCode(204);
    }

    @Test
    @Order(6)
    public void testGetBookById_NotFoundAfterDeletion() {
        given()
                .when().get("/api/books/" + bookId)
                .then()
                .statusCode(404);
    }

    @Test
    @Order(7)
    public void testCreateBook_WithInvalidData_ShouldReturnBadRequest() {
        String invalidBookJson = """
            {
                "title": "",
                "isbn": "123"
            }
            """;
        given()
                .contentType(ContentType.JSON)
                .body(invalidBookJson)
                .when().post("/api/books")
                .then()
                .statusCode(400);
    }
}
