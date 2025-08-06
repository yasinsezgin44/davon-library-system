package com.davon.library.controller;

import com.davon.library.model.*;
import com.davon.library.service.UserService;
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
    public Response getUsers(@QueryParam("filter") String filter) {
        try {
            List<User> users;
            if (filter == null || filter.isEmpty()) {
                users = userService.getAllUsers(); // Use getAllUsers instead of searchUsers("")
            } else {
                users = userService.searchUsers(filter);
            }
            return Response.ok(users).build();
        } catch (Exception e) {
            e.printStackTrace(); // Add stack trace for debugging
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving users: " + e.getMessage() + "\nStack trace: " + e.getStackTrace()[0])
                    .build();
        }
    }

    @POST
    @Operation(summary = "Create new user", description = "Add a new user to the system")
    public Response createUser(User userData) {
        try {
            User newUser = userService.createUser(userData);
            return Response.status(Response.Status.CREATED).entity(newUser).build();
        } catch (UserService.UserServiceException e) {
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
        } catch (UserService.UserServiceException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Operation(summary = "Delete user", description = "Deactivate a user")
    public Response deleteUser(@PathParam("id") Long id) {
        try {
            User user = userService.findById(id);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            boolean success = userService.deactivateUser(id);
            if (success) {
                return Response.noContent().build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        } catch (UserService.UserServiceException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID")
    public Response getUserById(@PathParam("id") Long id) {
        User user = userService.findById(id);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(user).build();
    }

    @GET
    @Path("/search")
    @Operation(summary = "Search users", description = "Search for users by various criteria")
    public Response searchUsers(@QueryParam("q") String query) {
        try {
            List<User> users = userService.searchUsers(query != null ? query : "");
            return Response.ok(users).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error searching users: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Path("/count")
    @Operation(summary = "Count users", description = "Get the total number of users")
    public Response countUsers() {
        long count = userService.countUsers();
        return Response.ok(Map.of("count", count)).build();
    }
}
