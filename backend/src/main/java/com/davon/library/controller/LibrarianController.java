package com.davon.library.controller;

import com.davon.library.model.Loan;
import com.davon.library.service.LibrarianService;
import com.davon.library.mapper.LoanMapper;
import com.davon.library.dto.LoanResponseDTO;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/librarian")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Librarian Operations", description = "Operations performed by librarians")
@SecurityRequirement(name = "jwt")
public class LibrarianController {

    @Inject
    LibrarianService librarianService;

    @POST
    @Path("/checkout")
    @RolesAllowed({ "LIBRARIAN", "ADMIN", "MEMBER" })
    @Operation(summary = "Checkout a book for a member")
    public Response checkoutBook(@QueryParam("bookId") Long bookId, @QueryParam("userId") Long userId) {
        try {
            LoanResponseDTO loan = librarianService.checkoutBookForMember(bookId, userId);
            return Response.ok(loan).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
