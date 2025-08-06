package com.davon.library.controller;

import com.davon.library.dto.ReservationRequest;
import com.davon.library.model.Reservation;
import com.davon.library.service.ReservationService;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@QuarkusTest
class ReservationControllerTest {

    @InjectMock
    ReservationService reservationService;

    private Reservation reservation;

    @BeforeEach
    void setUp() {
        reservation = new Reservation();
        reservation.setId(1L);
    }

    @Test
    @TestSecurity(user = "testUser", roles = {"MEMBER"})
    void testReserveBook() {
        when(reservationService.createReservation(anyLong(), anyLong())).thenReturn(reservation);

        ReservationRequest request = new ReservationRequest();
        request.setBookId(1L);

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/api/reservations")
                .then()
                .statusCode(201)
                .body("id", is(1));
    }

    @Test
    void testReserveBook_unauthorized() {
        given()
                .contentType(ContentType.JSON)
                .body(new ReservationRequest())
                .when().post("/api/reservations")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "testAdmin", roles = {"ADMIN"})
    void testReserveBook_forbidden() {
        given()
                .contentType(ContentType.JSON)
                .body(new ReservationRequest())
                .when().post("/api/reservations")
                .then()
                .statusCode(403);
    }
}
