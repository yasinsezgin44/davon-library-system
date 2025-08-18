package com.davon.library.controller;

import com.davon.library.dto.LoanResponseDTO;
import com.davon.library.service.LoanService;
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

@Path("/api/loans")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Loan Operations", description = "Operations related to borrowing and returning books")
@SecurityRequirement(name = "jwt")
public class LoanController {

    @Inject
    LoanService loanService;

    @POST
    @Path("/borrow")
    @RolesAllowed("MEMBER")
    @Operation(summary = "Borrow a book")
    public Response borrowBook(@QueryParam("bookId") Long bookId, @Context SecurityContext securityContext) {
        try {
            String username = securityContext.getUserPrincipal().getName();
            LoanResponseDTO loan = loanService.borrowBook(bookId, username);
            return Response.ok(loan).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
