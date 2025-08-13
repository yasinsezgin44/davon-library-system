package com.davon.library.controller;

import com.davon.library.service.UserService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import io.quarkus.test.security.TestSecurity;

import static io.restassured.RestAssured.given;

@QuarkusTest
class UserControllerTest {

    @InjectMock
    UserService userService;

    @Test
    @TestSecurity(user = "admin", roles = { "ADMIN" })
    void testGetUsersEndpoint() {
        given()
                .when()
                .get("/api/users")
                .then()
                .statusCode(200);
    }
}
