package com.smartcampus.api;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * TASK 1.1: Project & Application Configuration 
 * Establishes the API's versioned entry point at /api/v1.
 */
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {
    
}
