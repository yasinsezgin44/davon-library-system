package com.davon.library.controller;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import java.util.UUID;

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
                // Generate unique email to avoid constraint violations
                String uniqueEmail = "test-" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
                String uniqueUsername = "testuser-" + UUID.randomUUID().toString().substring(0, 8);

                // Create a user using JSON with userType field for Jackson deserialization
                String userJson = """
                                {
                                    "userType": "member",
                                    "username": "%s",
                                    "passwordHash": "hashed_password",
                                    "fullName": "Test User",
                                    "email": "%s",
                                    "phoneNumber": "123-456-7890",
                                    "active": true,
                                    "status": "ACTIVE",
                                    "membershipStartDate": "2024-01-01",
                                    "membershipEndDate": "2025-01-01",
                                    "address": "123 Test St"
                                }
                                """.formatted(uniqueUsername, uniqueEmail);

                // Create a user
                given()
                                .contentType(ContentType.JSON)
                                .body(userJson)
                                .when().post("/api/users")
                                .then()
                                .statusCode(201)
                                .contentType(ContentType.JSON)
                                .body("username", is(uniqueUsername))
                                .body("fullName", is("Test User"));
        }

        @Test
        void testSearchUsers() {
                // Generate unique identifiers to avoid constraint violations
                String uniqueEmail = "search-" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
                String uniqueUsername = "searchuser-" + UUID.randomUUID().toString().substring(0, 8);

                // First create a user to search for using JSON
                String userJson = """
                                {
                                    "userType": "member",
                                    "username": "%s",
                                    "passwordHash": "password",
                                    "fullName": "Search Test User",
                                    "email": "%s",
                                    "phoneNumber": "555-555-5555",
                                    "active": true,
                                    "status": "ACTIVE"
                                }
                                """.formatted(uniqueUsername, uniqueEmail);

                given()
                                .contentType(ContentType.JSON)
                                .body(userJson)
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

        @Test
        void testUpdateUser() {
                // First create a user
                String uniqueEmail = "update-" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
                String uniqueUsername = "updateuser-" + UUID.randomUUID().toString().substring(0, 8);

                String userJson = """
                                {
                                    "userType": "member",
                                    "username": "%s",
                                    "passwordHash": "hashed_password",
                                    "fullName": "Original Name",
                                    "email": "%s",
                                    "phoneNumber": "123-456-7890",
                                    "active": true,
                                    "status": "ACTIVE"
                                }
                                """.formatted(uniqueUsername, uniqueEmail);

                String userId = given()
                                .contentType(ContentType.JSON)
                                .body(userJson)
                                .when().post("/api/users")
                                .then()
                                .statusCode(201)
                                .extract()
                                .jsonPath()
                                .getString("id");

                // Update the user
                String updatedUserJson = """
                                {
                                    "userType": "member",
                                    "username": "%s",
                                    "passwordHash": "new_hashed_password",
                                    "fullName": "Updated Name",
                                    "email": "%s",
                                    "phoneNumber": "987-654-3210",
                                    "active": true,
                                    "status": "ACTIVE"
                                }
                                """.formatted(uniqueUsername, uniqueEmail);

                given()
                                .contentType(ContentType.JSON)
                                .body(updatedUserJson)
                                .when().put("/api/users/" + userId)
                                .then()
                                .statusCode(200)
                                .body("fullName", is("Updated Name"))
                                .body("phoneNumber", is("987-654-3210"));
        }

        @Test
        void testGetUserById() {
                // First create a user
                String uniqueEmail = "get-" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
                String uniqueUsername = "getuser-" + UUID.randomUUID().toString().substring(0, 8);

                String userJson = """
                                {
                                    "userType": "member",
                                    "username": "%s",
                                    "passwordHash": "hashed_password",
                                    "fullName": "Get Test User",
                                    "email": "%s",
                                    "phoneNumber": "555-123-4567",
                                    "active": true,
                                    "status": "ACTIVE"
                                }
                                """.formatted(uniqueUsername, uniqueEmail);

                String userId = given()
                                .contentType(ContentType.JSON)
                                .body(userJson)
                                .when().post("/api/users")
                                .then()
                                .statusCode(201)
                                .extract()
                                .jsonPath()
                                .getString("id");

                // Get the user by ID
                given()
                                .when().get("/api/users/" + userId)
                                .then()
                                .statusCode(200)
                                .body("fullName", is("Get Test User"))
                                .body("email", is(uniqueEmail))
                                .body("username", is(uniqueUsername));
        }

        @Test
        void testDeleteUser() {
                // First create a user
                String uniqueEmail = "delete-" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
                String uniqueUsername = "deleteuser-" + UUID.randomUUID().toString().substring(0, 8);

                String userJson = """
                                {
                                    "userType": "member",
                                    "username": "%s",
                                    "passwordHash": "hashed_password",
                                    "fullName": "Delete Test User",
                                    "email": "%s",
                                    "phoneNumber": "555-999-8888",
                                    "active": true,
                                    "status": "ACTIVE"
                                }
                                """.formatted(uniqueUsername, uniqueEmail);

                String userId = given()
                                .contentType(ContentType.JSON)
                                .body(userJson)
                                .when().post("/api/users")
                                .then()
                                .statusCode(201)
                                .extract()
                                .jsonPath()
                                .getString("id");

                // Delete (deactivate) the user
                given()
                                .when().delete("/api/users/" + userId)
                                .then()
                                .statusCode(204); // No Content for successful deletion

                // Verify the user is deactivated by checking its active status
                given()
                                .when().get("/api/users/" + userId)
                                .then()
                                .statusCode(200)
                                .body("active", is(false)); // User should be deactivated, not deleted
        }

        @Test
        void testCreateUserWithInvalidData() {
                // Test with missing required fields
                String invalidUserJson = """
                                {
                                    "userType": "member",
                                    "username": "",
                                    "email": "invalid-email",
                                    "fullName": ""
                                }
                                """;

                given()
                                .contentType(ContentType.JSON)
                                .body(invalidUserJson)
                                .when().post("/api/users")
                                .then()
                                .statusCode(400); // Bad Request for validation errors
        }

        @Test
        void testGetUsersByStatus() {
                given()
                                .param("status", "ACTIVE")
                                .when().get("/api/users")
                                .then()
                                .statusCode(200)
                                .contentType(ContentType.JSON);
        }

        @Test
        void testGetUsersByUserType() {
                given()
                                .param("userType", "member")
                                .when().get("/api/users")
                                .then()
                                .statusCode(200)
                                .contentType(ContentType.JSON);
        }
}