package com.smartcampus.api;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * TASK 1.1: REST API Base Path Configuration.
 * This class configures the application entry point at /api/v1/
 */
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {
    // The JAX-RS container scans this package for resources and mappers automatically.
}