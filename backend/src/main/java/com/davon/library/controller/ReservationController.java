package com.davon.library.controller;

import com.davon.library.dto.ReservationRequest;
import com.davon.library.dto.ReservationSummary;
import com.davon.library.dto.UpdateReservationStatusRequest;
import com.davon.library.model.Reservation;
import com.davon.library.model.enums.ReservationStatus;
import com.davon.library.service.ReservationService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

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

    @GET
    @RolesAllowed({ "ADMIN", "LIBRARIAN" })
    @Operation(summary = "List all reservations")
    @SecurityRequirement(name = "jwt")
    public List<ReservationSummary> getAllReservations() {
        return reservationService.getAllReservations().stream()
                .map(r -> new ReservationSummary(
                        r.getId(),
                        r.getBook().getTitle(),
                        r.getMember().getUser().getFullName(),
                        r.getReservationTime(),
                        r.getStatus().name()))
                .collect(Collectors.toList());
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "ADMIN", "LIBRARIAN" })
    @Operation(summary = "Update reservation status")
    @SecurityRequirement(name = "jwt")
    public Response updateReservationStatus(@PathParam("id") Long id, UpdateReservationStatusRequest request) {
        ReservationStatus newStatus = ReservationStatus.valueOf(request.getStatus());
        reservationService.updateReservationStatus(id, newStatus);
        return Response.noContent().build();
    }
}
