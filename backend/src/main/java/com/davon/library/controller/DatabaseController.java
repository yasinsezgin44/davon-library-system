package com.davon.library.controller;

import com.davon.library.database.DatabaseConnectionManager;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * REST controller for database-related operations.
 */
@Path("/api/database")
@Produces(MediaType.APPLICATION_JSON)
public class DatabaseController {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseController.class);

    @Inject
    DatabaseConnectionManager connectionManager;

    @GET
    @Path("/status")
    public Response getDatabaseStatus() {
        try {
            boolean isAccessible = connectionManager.isDatabaseAccessible();
            String databaseInfo = connectionManager.getDatabaseInfo();
            
            Map<String, Object> status = Map.of(
                "accessible", isAccessible,
                "database", databaseInfo,
                "timestamp", System.currentTimeMillis()
            );
            
            if (isAccessible) {
                return Response.ok(status).build();
            } else {
                return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                    .entity(status)
                    .build();
            }
        } catch (Exception e) {
            logger.error("Error checking database status", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

    @GET
    @Path("/info")
    public Response getDatabaseInfo() {
        try {
            String databaseInfo = connectionManager.getDatabaseInfo();
            
            return Response.ok(Map.of(
                "database", databaseInfo,
                "type", "Microsoft SQL Server",
                "driver", "MSSQL JDBC Driver"
            )).build();
            
        } catch (Exception e) {
            logger.error("Error getting database info", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }
}
