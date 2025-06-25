package com.davon.library.controller;

import com.davon.library.model.Member;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class UserControllerTest {

    @Test
    void testGetAllUsers() {
        given()
                .when().get("/api/users")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    void testCreateAndGetUser() {
        Member member = Member.builder()
                .username("testuser")
                .passwordHash("hashed_password")
                .fullName("Test User")
                .email("test@example.com")
                .phoneNumber("123-456-7890")
                .active(true)
                .status("ACTIVE")
                .membershipStartDate(LocalDate.now())
                .membershipEndDate(LocalDate.now().plusYears(1))
                .address("123 Test St")
                .build();

        // Create a user
        given()
                .contentType(ContentType.JSON)
                .body(member)
                .when().post("/api/users")
                .then()
                .statusCode(201)
                .contentType(ContentType.JSON)
                .body("username", is("testuser"))
                .body("fullName", is("Test User"));
    }

    @Test
    void testSearchUsers() {
        // First create a user to search for
        Member member = Member.builder()
                .username("searchuser")
                .passwordHash("password")
                .fullName("Search Test User")
                .email("search@example.com")
                .phoneNumber("555-555-5555")
                .active(true)
                .status("ACTIVE")
                .build();

        given()
                .contentType(ContentType.JSON)
                .body(member)
                .when().post("/api/users")
                .then()
                .statusCode(201);

        // Now search for it
        given()
                .param("q", "Search")
                .when().get("/api/users/search")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThanOrEqualTo(1));
    }

    @Test
    void testGetUserByIdNotFound() {
        given()
                .when().get("/api/users/999")
                .then()
                .statusCode(404);
    }

    @Test
    void testDeleteUserNotFound() {
        given()
                .when().delete("/api/users/999")
                .then()
                .statusCode(404);
    }

    @Test
    void testGetUsersWithFilter() {
        given()
                .param("filter", "test")
                .when().get("/api/users")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }
}