package com.davon.library.controller;

import com.davon.library.dto.ReservationRequestDTO;
import com.davon.library.dto.ReservationResponseDTO;
import com.davon.library.mapper.ReservationMapper;
import com.davon.library.model.Reservation;
import com.davon.library.model.enums.ReservationStatus;
import com.davon.library.service.ReservationService;
import com.davon.library.repository.MemberRepository;
import com.davon.library.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.transaction.Transactional;
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
import jakarta.annotation.security.PermitAll;

@Path("/api/reservations")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Reservations", description = "Book reservation operations")
public class ReservationController {

    @Inject
    ReservationService reservationService;

    @Inject
    MemberRepository memberRepository;

    @Inject
    UserService userService;

    @POST
    @RolesAllowed("MEMBER")
    @Operation(summary = "Reserve a book")
    @SecurityRequirement(name = "jwt")
    @Transactional
    public Response reserveBook(@Valid ReservationRequestDTO request, @Context SecurityContext securityContext) {
        String username = securityContext.getUserPrincipal().getName();
        Long memberId = memberRepository.findByUsername(username)
                .map(m -> m.getId())
                .orElseGet(() -> userService.getUserByUsername(username)
                        .orElseThrow(() -> new NotFoundException("User not found with username: " + username))
                        .getId());
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
    public Response updateReservationStatus(@PathParam("id") Long id, com.fasterxml.jackson.databind.node.ObjectNode body) {
        if (body == null || body.get("status") == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing status").build();
        }
        ReservationStatus status;
        try {
            status = ReservationStatus.valueOf(body.get("status").asText());
        } catch (IllegalArgumentException ex) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid status").build();
        }
        reservationService.updateReservationStatus(id, status);
        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    @RolesAllowed({ "ADMIN", "LIBRARIAN" })
    @Operation(summary = "Delete/cancel a reservation")
    @SecurityRequirement(name = "jwt")
    @Transactional
    public Response deleteReservation(@PathParam("id") Long id) {
        reservationService.cancelReservation(id);
        return Response.noContent().build();
    }

    @GET
    @Path("/me")
    @RolesAllowed({ "MEMBER", "LIBRARIAN", "ADMIN" })
    @Operation(summary = "Get current user's reservations")
    @SecurityRequirement(name = "jwt")
    @Transactional
    public List<ReservationResponseDTO> getMyReservations(@Context SecurityContext securityContext) {
        String username = securityContext.getUserPrincipal().getName();
        Long memberId = memberRepository.findByUsername(username)
                .map(m -> m.getId())
                .orElseGet(() -> userService.getUserByUsername(username)
                        .orElseThrow(() -> new NotFoundException("User not found with username: " + username))
                        .getId());
        return reservationService.getReservationsByMember(memberId).stream()
                .map(ReservationMapper::toResponseDTO)
                .collect(java.util.stream.Collectors.toList());
    }
}
