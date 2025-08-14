package com.davon.library.controller;

import com.davon.library.dto.UserCreateRequest;
import com.davon.library.service.AdminService;
import com.davon.library.service.UserService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;
import com.davon.library.model.User;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import com.davon.library.repository.UserRepository;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import io.quarkus.elytron.security.common.BcryptUtil;
import com.davon.library.mapper.UserMapper;
import com.davon.library.dto.UserRequestDTO;
import com.davon.library.model.enums.UserStatus;
import com.davon.library.model.Role;
import com.davon.library.repository.RoleRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doNothing;

import java.util.Optional;
import static org.mockito.Mockito.mock;
import io.quarkus.test.TestTransaction;

@QuarkusTest
class AdminControllerTest {

        @InjectMock
        AdminService adminService;

        @InjectMock
        UserService userService;

        @Test
        @TestSecurity(user = "admin", roles = { "ADMIN" })
        void testCreateUserEndpoint() {
                UserCreateRequest request = new UserCreateRequest("anotheruser", "password", "Another User",
                                "anotheruser@example.com", "MEMBER");

                User user = new User();
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
                                .body("email", is("anotheruser@example.com"));
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
                                .body(is(notNullValue()));
        }

        @Test
        @TestSecurity(user = "admin", roles = { "ADMIN" })
        void testDeleteUserEndpoint() {
                doNothing().when(adminService).deleteUser(1L);
                given()
                                .when()
                                .delete("/api/admin/users/1")
                                .then()
                                .statusCode(204);
        }

        @Test
        @TestSecurity(user = "admin", roles = { "ADMIN" })
        @TestTransaction
        void testAssignRoleToUserEndpoint() {
                AdminService adminService = mock(AdminService.class);
                UserService userService = mock(UserService.class);

                User testUser = new User();
                testUser.setId(1L);
                testUser.setUsername("testuser");

                Role testRole = new Role();
                testRole.setId(1L);
                testRole.setName("LIBRARIAN");

                testUser.getRoles().add(testRole);

                when(adminService.assignRoleToUser(1L, "LIBRARIAN")).thenReturn(testUser);

                given()
                                .when()
                                .post("/api/admin/users/1/roles/LIBRARIAN")
                                .then()
                                .statusCode(200)
                                .body("roles[0].name", is("LIBRARIAN"));
        }
}
