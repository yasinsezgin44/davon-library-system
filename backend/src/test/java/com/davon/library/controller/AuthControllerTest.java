package com.davon.library.controller;

import com.davon.library.dto.LoginRequest;
import com.davon.library.model.Role;
import com.davon.library.model.User;
import com.davon.library.service.AuthenticationService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.Mockito.when;

@QuarkusTest
class AuthControllerTest {

    @InjectMock
    AuthenticationService authenticationService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        Role role = new Role();
        role.setName("MEMBER");
        user.setRoles(Set.of(role));
    }

    @Test
    void testLogin() {
        when(authenticationService.authenticate("testuser", "password")).thenReturn(user);

        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/api/auth/login")
                .then()
                .statusCode(200)
                .body("token", notNullValue());
    }
}
