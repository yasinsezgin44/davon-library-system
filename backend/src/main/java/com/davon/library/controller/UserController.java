package com.davon.library.controller;

import com.davon.library.model.*;
import com.davon.library.service.UserService;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Users", description = "User management operations")
public class UserController {

    @Inject
    UserService userService;

    @GET
    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    public List<User> getUsers(@QueryParam("filter") String filter) {
        if (filter == null || filter.isEmpty()) {
            return userService.searchUsers("");
        }
        return userService.searchUsers(filter);
    }

    @POST
    @Operation(summary = "Create new user", description = "Add a new user to the system")
    public Response createUser(User userData) {
        try {
            User newUser = userService.createUser(userData);
            return Response.status(Response.Status.CREATED).entity(newUser).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT
    @Path("/{id}")
    @Operation(summary = "Update user", description = "Update an existing user")
    public Response updateUser(@PathParam("id") Long id, User userData) {
        try {
            User user = userService.findById(id);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            User updatedUser = userService.updateUser(id, userData);
            return Response.ok(updatedUser).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete user", description = "Deactivate a user")
    public Response deleteUser(@PathParam("id") Long id) {
        try {
            userService.deactivateUser(id);
            return Response.noContent().build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID")
    public Response getUserById(@PathParam("id") Long id) {
        try {
            User user = userService.findById(id);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            return Response.ok(user).build();
        } catch (Exception e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("/search")
    @Operation(summary = "Search users", description = "Search for users by various criteria")
    public List<User> searchUsers(@QueryParam("q") String query) {
        return userService.searchUsers(query != null ? query : "");
    }
}
