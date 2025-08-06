package com.davon.library;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ApplicationIntegrationTest {

    @Test
    @Order(1)
    public void testApplicationHealthEndpoint() {
        given()
                .when().get("/q/health")
                .then()
                .statusCode(200)
                .body("status", is("UP"));
    }

    @Test
    @Order(2)
    public void testBooksEndpoint() {
        given()
                .when().get("/api/books")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    @Order(3)
    public void testCreateAndRetrieveBook() {
        java.util.Random random = new java.util.Random();
        long randomISBN = 1000000000L + (long) (random.nextDouble() * 9000000000L);
        String uniqueISBN = String.valueOf(randomISBN);
        String newBookJson = """
                {
                    "title": "Integration Test Book",
                    "isbn": "%s",
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
                .extract()
                .jsonPath()
                .getString("id");

        given()
                .when()
                .get("/api/books/" + bookId)
                .then()
                .statusCode(200)
                .body("title", equalTo("Integration Test Book"))
                .body("isbn", equalTo(uniqueISBN))
                .body("publicationYear", equalTo(2024));
    }
}
