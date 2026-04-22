package com.westminster.smart.campus.api;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * TASK 1.1: REST API Versioning
 * Establishing the base URI for the API at /api/v1
 */
@ApplicationPath("/api/v1")
public class JAXRSConfiguration extends Application {
    // Empty body — the annotation does everything
}
