package com.davon.library.controller;

import com.davon.library.dto.LoanResponseDTO;
import com.davon.library.mapper.LoanMapper;
import com.davon.library.model.enums.LoanStatus;
import com.davon.library.service.LoanService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

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
    @RolesAllowed({ "ADMIN", "LIBRARIAN" })
    @Operation(summary = "List all active loans")
    @Transactional
    public List<LoanResponseDTO> listActiveLoans() {
        return loanService.getLoansByStatus(LoanStatus.ACTIVE)
                .stream()
                .map(LoanMapper::toResponseDTO)
                .collect(Collectors.toList());
    }
}
