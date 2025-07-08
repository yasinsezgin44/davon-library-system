package com.davon.library;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.greaterThan;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApplicationIntegrationTest {

    @Test
    @Order(1)
    public void testApplicationHealthEndpoint() {
        given()
                .when().get("/api/health")
                .then()
                .statusCode(200)
                .body("service", is("Davon Library System"))
                .body("version", is("1.0.0"))
                .body("status", is("UP"));
    }

    @Test
    @Order(2)
    public void testQuarkusHealthEndpoint() {
        given()
                .when().get("/q/health")
                .then()
                .statusCode(200)
                .body("status", is("UP"))
                .body("checks.size()", greaterThan(0));
    }

    @Test
    @Order(3)
    public void testDatabaseConnectivity() {
        given()
                .when().get("/q/health")
                .then()
                .statusCode(200)
                .body("checks.find { it.name == 'Database connections health check' }.status", is("UP"))
                .body("checks.find { it.name == 'Database' }.status", is("UP"));
    }

    @Test
    @Order(4)
    public void testBooksEndpoint() {
        given()
                .when().get("/api/books")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThan(0))
                .body("[0].id", notNullValue())
                .body("[0].title", notNullValue());
    }

    @Test
    @Order(5)
    public void testCreateAndRetrieveBook() {
        // Create a new book with unique ISBN using timestamp
        String uniqueISBN = "978" + System.currentTimeMillis() % 1000000000L; // Ensure 13 digits
        String newBookJson = """
                {
                    "title": "Integration Test Book",
                    "ISBN": "%s",
                    "publicationYear": 2024,
                    "description": "A book created during integration testing",
                    "pages": 200
                }
                """.formatted(uniqueISBN);

        String bookId = given()
                .contentType(ContentType.JSON)
                .body(newBookJson)
                .when()
                .post("/api/books")
                .then()
                .statusCode(201)
                .body("title", is("Integration Test Book"))
                .body("ISBN", is(uniqueISBN))
                .body("publicationYear", is(2024))
                .body("id", notNullValue())
                .extract()
                .path("id")
                .toString();

        // Retrieve the created book
        given()
                .when()
                .get("/api/books/" + bookId)
                .then()
                .statusCode(200)
                .body("title", is("Integration Test Book"))
                .body("ISBN", is(uniqueISBN))
                .body("publicationYear", is(2024));
    }

    @Test
    @Order(6)
    public void testDatabaseReadyness() {
        // Test the readiness endpoint which should check database health
        given()
                .when().get("/q/health/ready")
                .then()
                .statusCode(200)
                .body("status", is("UP"));
    }

    @Test
    @Order(7)
    public void testDatabaseLiveness() {
        // Test the liveness endpoint
        given()
                .when().get("/q/health/live")
                .then()
                .statusCode(200)
                .body("status", is("UP"));
    }
}