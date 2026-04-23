package com.smartcampus.api;

import org.glassfish.jersey.server.ResourceConfig;
import javax.ws.rs.ApplicationPath;

/**
 * TASK 1.1: REST API Versioning
 * Establishing the base URI for the API at /api/v1
 */
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends ResourceConfig {
    public SmartCampusApplication() {
        // This automatically finds your @Path and @Provider classes
        packages("com.smartcampus"); 
    }
}