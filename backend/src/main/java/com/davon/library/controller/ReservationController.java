package com.davon.library.controller;

import com.davon.library.dto.ReservationRequest;
import com.davon.library.model.Reservation;
import com.davon.library.service.ReservationService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/reservations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Reservations", description = "Book reservation operations")
public class ReservationController {

    @Inject
    ReservationService reservationService;

    @POST
    @RolesAllowed("MEMBER")
    @Operation(summary = "Reserve a book")
    @SecurityRequirement(name = "jwt")
    public Response reserveBook(ReservationRequest request, @Context SecurityContext securityContext) {
        // In a real application, you would get the member ID from the security context
        // For now, we will use a hardcoded value for demonstration purposes
        Long memberId = 1L;
        Reservation reservation = reservationService.createReservation(memberId, request.getBookId());
        return Response.status(Response.Status.CREATED).entity(reservation).build();
    }
}
