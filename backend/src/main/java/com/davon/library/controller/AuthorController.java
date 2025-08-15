package com.davon.library.controller;

import com.davon.library.dto.AuthorDTO;
import com.davon.library.mapper.AuthorMapper;
import com.davon.library.service.AuthorService;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.stream.Collectors;

@Path("/api/authors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Authors", description = "Author management operations")
public class AuthorController {

    @Inject
    AuthorService authorService;

    @GET
    @Operation(summary = "Get all authors")
    @PermitAll
    public List<AuthorDTO> getAllAuthors() {
        return authorService.getAllAuthors().stream()
                .map(AuthorMapper::toDTO)
                .collect(Collectors.toList());
    }
}
