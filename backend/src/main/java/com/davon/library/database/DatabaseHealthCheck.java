package com.davon.library.database;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Health check for database connectivity.
 */
@Readiness
@ApplicationScoped
public class DatabaseHealthCheck implements HealthCheck {

    @Inject
    DatabaseConnectionManager connectionManager;

    @Override
    public HealthCheckResponse call() {
        try {
            boolean isAccessible = connectionManager.isDatabaseAccessible();
            String databaseInfo = connectionManager.getDatabaseInfo();

            if (isAccessible) {
                return HealthCheckResponse.up("Database").build();
            } else {
                return HealthCheckResponse.down("Database").build();
            }
        } catch (Exception e) {
            return HealthCheckResponse.down("Database").build();
        }
    }
}
