package com.davon.library.controller;

import com.davon.library.model.Loan;
import com.davon.library.model.enums.LoanStatus;
import com.davon.library.service.LoanService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

@Path("/api/admin/dashboard")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Admin Dashboard", description = "Admin reporting endpoints")
@SecurityRequirement(name = "jwt")
public class DashboardAdminController {

    @Inject
    LoanService loanService;

    @GET
    @Path("/loans/active")
    @RolesAllowed("ADMIN")
    @Operation(summary = "List all active loans")
    public List<Loan> listActiveLoans() {
        return loanService.getLoansByStatus(LoanStatus.ACTIVE);
    }
}
