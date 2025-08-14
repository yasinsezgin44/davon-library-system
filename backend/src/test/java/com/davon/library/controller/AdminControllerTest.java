package com.davon.library.controller;

import com.davon.library.dto.UserCreateRequest;
import com.davon.library.service.AdminService;
import com.davon.library.service.UserService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.davon.library.model.User;
import com.davon.library.model.Role;

import java.util.Collections;
import java.util.HashSet;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;

@QuarkusTest
class AdminControllerTest {

        @InjectMock
        AdminService adminService;

        @InjectMock
        UserService userService;

        @BeforeEach
        void setUp() {
                reset(adminService, userService);
        }

        @Test
        @TestSecurity(user = "admin", roles = { "ADMIN" })
        void testCreateUserEndpoint() {
                UserCreateRequest request = new UserCreateRequest("anotheruser", "password", "Another User",
                                "anotheruser@example.com", "MEMBER");

                User user = new User();
                user.setId(1L);
                user.setUsername(request.getUsername());
                user.setFullName(request.getFullName());
                user.setEmail(request.getEmail());
                Role memberRole = new Role();
                memberRole.setName("MEMBER");
                user.setRoles(java.util.Collections.singleton(memberRole));

                when(adminService.createUserWithRole(any(User.class), anyString())).thenReturn(user);

                given()
                                .contentType("application/json")
                                .body(request)
                                .when()
                                .post("/api/admin/users")
                                .then()
                                .statusCode(201)
                                .body("username", is("anotheruser"))
                                .body("fullName", is("Another User"))
                                .body("email", is("anotheruser@example.com"))
                                .body("roles", hasItem("MEMBER"));
        }

        @Test
        @TestSecurity(user = "admin", roles = { "ADMIN" })
        void testGetAllUsersEndpoint() {
                when(userService.getAllUsers()).thenReturn(java.util.Collections.emptyList());
                given()
                                .when()
                                .get("/api/admin/users")
                                .then()
                                .statusCode(200)
                                .body("size()", is(0));
        }

        @Test
        @TestSecurity(user = "admin", roles = { "ADMIN" })
        void testDeleteUserEndpoint() {
                doNothing().when(adminService).deleteUser(any());
                given()
                                .when()
                                .delete("/api/admin/users/1")
                                .then()
                                .statusCode(204);
        }

        @Test
        @TestSecurity(user = "admin", roles = { "ADMIN" })
        void testAssignRoleToUserEndpoint() {
                User testUser = new User();
                testUser.setId(1L);
                testUser.setUsername("testuser");
                testUser.setFullName("Test User");
                testUser.setEmail("testuser@example.com");

                Role testRole = new Role();
                testRole.setId(1L);
                testRole.setName("LIBRARIAN");

                testUser.setRoles(new HashSet<>(Collections.singletonList(testRole)));

                when(adminService.assignRoleToUser(any(), any())).thenReturn(testUser);

                given()
                                .when()
                                .post("/api/admin/users/1/roles/LIBRARIAN")
                                .then()
                                .statusCode(200)
                                .body("username", is("testuser"))
                                .body("roles", hasItem("LIBRARIAN"));
        }
}
