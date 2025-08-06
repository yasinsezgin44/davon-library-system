package com.davon.library;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class for the Davon Library Management System.
 */
@QuarkusMain
@ApplicationScoped
public class LibraryManagementApp implements QuarkusApplication {

    private static final Logger logger = LoggerFactory.getLogger(LibraryManagementApp.class);

    public static void main(String[] args) {
        Quarkus.run(LibraryManagementApp.class, args);
    }

    @Override
    public int run(String... args) throws Exception {
        logger.info("Davon Library Management System started successfully!");
        logger.info("Visit http://localhost:8082 for the application");
        logger.info("Visit http://localhost:8082/q/swagger-ui for API documentation");
        logger.info("Visit http://localhost:8082/q/health for health checks");

        Quarkus.waitForExit();
        return 0;
    }
}