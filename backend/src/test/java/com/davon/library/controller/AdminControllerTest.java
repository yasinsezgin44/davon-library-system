package com.davon.library.controller;

import com.davon.library.dto.UserCreateRequest;
import com.davon.library.service.AdminService;
import com.davon.library.service.UserService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;
import com.davon.library.model.User;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;

@QuarkusTest
class AdminControllerTest {

    @InjectMock
    AdminService adminService;

    @InjectMock
    UserService userService;

    @Test
    @TestSecurity(user = "admin", roles = { "ADMIN" })
    void testCreateUserEndpoint() {
        UserCreateRequest request = new UserCreateRequest("newuser", "password", "New User", "newuser@example.com", "MEMBER");

        given()
                .contentType("application/json")
                .body(request)
                .when()
                .post("/api/admin/users")
                .then()
                .statusCode(201);
    }

    @Test
    @TestSecurity(user = "admin", roles = { "ADMIN" })
    void testGetAllUsersEndpoint() {
        given()
                .when()
                .get("/api/admin/users")
                .then()
                .statusCode(200);
    }

    @Test
    @TestSecurity(user = "admin", roles = { "ADMIN" })
    void testDeleteUserEndpoint() {
        given()
                .when()
                .delete("/api/admin/users/1")
                .then()
                .statusCode(204);
    }

    @Test
    @TestSecurity(user = "admin", roles = { "ADMIN" })
    void testAssignRoleToUserEndpoint() {
        when(adminService.assignRoleToUser(anyLong(), anyString())).thenReturn(new User());
        given()
                .when()
                .post("/api/admin/users/1/roles/LIBRARIAN")
                .then()
                .statusCode(200);
    }
}
