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

            if (isAccessible) {
                return HealthCheckResponse.up("Database");
            } else {
                return HealthCheckResponse.down("Database");
            }
        } catch (Exception e) {
            return HealthCheckResponse.down("Database");
        }
    }
}
