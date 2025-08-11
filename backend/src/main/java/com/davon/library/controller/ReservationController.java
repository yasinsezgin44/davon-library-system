package com.davon.library.controller;

import com.davon.library.dto.ReservationRequestDTO;
import com.davon.library.dto.ReservationResponseDTO;
import com.davon.library.mapper.ReservationMapper;
import com.davon.library.model.Reservation;
import com.davon.library.model.enums.ReservationStatus;
import com.davon.library.service.ReservationService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
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
    public Response reserveBook(@Valid ReservationRequestDTO request, @Context SecurityContext securityContext) {
        // In a real application, you would get the member ID from the security context
        // For now, we will use a hardcoded value for demonstration purposes
        Long memberId = 1L;
        Reservation reservation = reservationService.createReservation(memberId, request.bookId());
        return Response.status(Response.Status.CREATED).entity(ReservationMapper.toResponseDTO(reservation)).build();
    }

    @GET
    @RolesAllowed({ "ADMIN", "LIBRARIAN" })
    @Operation(summary = "List all reservations")
    @SecurityRequirement(name = "jwt")
    public List<ReservationResponseDTO> getAllReservations() {
        return reservationService.getAllReservations().stream()
                .map(ReservationMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @PUT
    @Path("/{id}")
    @RolesAllowed({ "ADMIN", "LIBRARIAN" })
    @Operation(summary = "Update reservation status")
    @SecurityRequirement(name = "jwt")
    public Response updateReservationStatus(@PathParam("id") Long id, @Valid ReservationRequestDTO request) {
        reservationService.updateReservationStatus(id, request.status());
        return Response.noContent().build();
    }
}
