package com.davon.library.controller;

import com.davon.library.dto.UserCreateRequest;
import com.davon.library.model.User;
import com.davon.library.service.AdminService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@QuarkusTest
class AdminControllerTest {

    @InjectMock
    AdminService adminService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
    }

    @Test
    @TestSecurity(user = "testAdmin", roles = {"ADMIN"})
    void testCreateUser() {
        when(adminService.createUserWithRole(any(User.class), anyString())).thenReturn(user);

        UserCreateRequest request = new UserCreateRequest();
        request.setUsername("testuser");
        request.setPassword("password");
        request.setRole("USER");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/api/admin/users")
                .then()
                .statusCode(201)
                .body("username", is("testuser"));
    }

    @Test
    void testCreateUser_unauthorized() {
        given()
                .contentType(ContentType.JSON)
                .body(new UserCreateRequest())
                .when().post("/api/admin/users")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"USER"})
    void testCreateUser_forbidden() {
        given()
                .contentType(ContentType.JSON)
                .body(new UserCreateRequest())
                .when().post("/api/admin/users")
                .then()
                .statusCode(403);
    }
}
