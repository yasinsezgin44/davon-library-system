package com.davon.library.controller;

import com.davon.library.dto.ReservationRequestDTO;
import com.davon.library.model.enums.ReservationStatus;
import com.davon.library.service.ReservationService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class ReservationControllerTest {

    @InjectMock
    ReservationService reservationService;

    @Test
    @TestSecurity(user = "member", roles = { "MEMBER" })
    void testReserveBookEndpoint() {
        ReservationRequestDTO requestDTO = new ReservationRequestDTO(1L, ReservationStatus.RESERVED);

        given()
                .contentType("application/json")
                .body(requestDTO)
                .when()
                .post("/api/reservations")
                .then()
                .statusCode(201);
    }

    @Test
    @TestSecurity(user = "librarian", roles = { "LIBRARIAN" })
    void testGetAllReservationsEndpoint() {
        given()
                .when()
                .get("/api/reservations")
                .then()
                .statusCode(200);
    }

    @Test
    @TestSecurity(user = "admin", roles = { "ADMIN" })
    void testUpdateReservationStatusEndpoint() {
        ReservationRequestDTO requestDTO = new ReservationRequestDTO(1L, ReservationStatus.CANCELLED);

        given()
                .contentType("application/json")
                .body(requestDTO)
                .when()
                .put("/api/reservations/1")
                .then()
                .statusCode(204);
    }
}
