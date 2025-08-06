package com.davon.library.controller;

import com.davon.library.dto.ProfileUpdateRequest;
import com.davon.library.model.User;
import com.davon.library.service.UserService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@QuarkusTest
class ProfileControllerTest {

    @InjectMock
    UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setFullName("Test User");
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"MEMBER"})
    void testGetMyProfile() {
        when(userService.getUserById(anyLong())).thenReturn(Optional.of(user));
        given()
                .when().get("/api/profile")
                .then()
                .statusCode(200)
                .body("fullName", is("Test User"));
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"MEMBER"})
    void testUpdateMyProfile() {
        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(user);

        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setFullName("Updated Name");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().put("/api/profile")
                .then()
                .statusCode(200)
                .body("fullName", is("Test User"));
    }

    @Test
    void testGetMyProfile_unauthorized() {
        given()
                .when().get("/api/profile")
                .then()
                .statusCode(401);
    }
}
