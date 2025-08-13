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

import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.doNothing;

@QuarkusTest
class AdminControllerTest {

    @InjectMock
    AdminService adminService;

    @InjectMock
    UserService userService;

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

        UserRequestDTO userRequestDTO = new UserRequestDTO(
                request.getUsername(),
                request.getPassword(),
                request.getFullName(),
                request.getEmail(),
                null,
                true,
                UserStatus.ACTIVE
        );

        User user = UserMapper.toEntity(userRequestDTO);

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
        User user = userRepository.findByUsername("newuser").orElseThrow();
        doNothing().when(adminService).deleteUser(user.getId());
        given()
                .when()
                .delete("/api/admin/users/" + user.getId())
                .then()
                .statusCode(204);
    }

    @Test
    @TestSecurity(user = "admin", roles = { "ADMIN" })
    void testAssignRoleToUserEndpoint() {
        User user = new User();
        user.setId(1L);
        Role role = new Role();
        role.setName("LIBRARIAN");
        user.setRoles(new java.util.HashSet<>(java.util.Collections.singletonList(role)));

        when(adminService.assignRoleToUser(1L, "LIBRARIAN")).thenReturn(user);

        given()
                .when()
                .post("/api/admin/users/1/roles/LIBRARIAN")
                .then()
                .statusCode(200);
    }
}
