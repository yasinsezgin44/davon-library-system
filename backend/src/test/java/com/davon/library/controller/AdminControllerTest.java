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

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;

@QuarkusTest
class AdminControllerTest {

    @Inject
    UserRepository userRepository;

    @BeforeEach
    @Transactional
    public void setUp() {
        User user = new User();
        user.setUsername("newuser");
        user.setPasswordHash(BcryptUtil.bcryptHash("password"));
        user.setFullName("New User");
        user.setEmail("newuser@example.com");
        userRepository.persist(user);
    }

    @AfterEach
    @Transactional
    public void tearDown() {
        userRepository.delete("username", "newuser");
    }

    @Test
    @TestSecurity(user = "admin", roles = { "ADMIN" })
    void testCreateUserEndpoint() {
        UserCreateRequest request = new UserCreateRequest("anotheruser", "password", "Another User",
                "anotheruser@example.com", "MEMBER");

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
        User user = userRepository.findByUsername("newuser").orElseThrow();
        given()
                .when()
                .delete("/api/admin/users/" + user.getId())
                .then()
                .statusCode(204);
    }

    @Test
    @TestSecurity(user = "admin", roles = { "ADMIN" })
    void testAssignRoleToUserEndpoint() {
        User user = userRepository.findByUsername("newuser").orElseThrow();
        given()
                .when()
                .post("/api/admin/users/" + user.getId() + "/roles/LIBRARIAN")
                .then()
                .statusCode(200);
    }
}
