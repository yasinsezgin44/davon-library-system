package com.davon.library.controller;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class TempAuthTest {

    @Test
    public void testRegisterAndLogin() {
        // Register a new user
        String registerPayload = "{\"username\":\"testuser\", \"password\":\"password\", \"fullName\":\"Test User\", \"email\":\"testuser@example.com\"}";
        given()
            .contentType("application/json")
            .body(registerPayload)
        .when()
            .post("/api/auth/register")
        .then()
            .statusCode(201);

        // Login with the new user
        String loginPayload = "{\"username\":\"testuser\", \"password\":\"password\"}";
        String token = given()
            .contentType("application/json")
            .body(loginPayload)
        .when()
            .post("/api/auth/login")
        .then()
            .statusCode(200)
            .extract().path("token");

        System.out.println("JWT Token: " + token);
    }
}
