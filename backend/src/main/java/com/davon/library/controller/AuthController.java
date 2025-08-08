package com.davon.library.controller;

import com.davon.library.dto.LoginRequest;
import com.davon.library.model.User;
import com.davon.library.service.AuthenticationService;
import io.smallrye.jwt.build.Jwt;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.NotAuthorizedException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Authentication", description = "User authentication operations")
public class AuthController {

    @Inject
    AuthenticationService authenticationService;

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    @POST
    @Path("/login")
    public Response login(LoginRequest request) {
        try {
            User user = authenticationService.authenticate(request.getUsername(), request.getPassword());
            
            Set<String> roles = user.getRoles().stream()
                    .map(role -> role.getName())
                    .collect(Collectors.toSet());

            String token = Jwt.issuer(issuer)
                    .subject(user.getUsername())
                    .groups(new HashSet<>(roles))
                    .expiresIn(Duration.ofHours(1))
                    .sign();

            return Response.ok(new AuthResponse(token)).build();
        } catch (NotAuthorizedException e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }
    }
}
