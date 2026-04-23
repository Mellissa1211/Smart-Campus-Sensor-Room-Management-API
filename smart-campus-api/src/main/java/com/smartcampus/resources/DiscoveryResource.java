package com.smartcampus.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * TASK 1.2: Discovery Endpoint
 * Providing API metadata and HATEOAS-style links to navigate the API.
 */
@Path("/")
public class DiscoveryResource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getDiscovery() {
        // Create navigation links for the client
        Map<String, String> links = new HashMap<>();
        links.put("rooms",   "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");

        // Prepare the JSON response body
        Map<String, Object> response = new HashMap<>();
        response.put("api_name",    "Smart Campus Management API");
        response.put("version",     "1.0");
        response.put("description", "HATEOAS enabled API for campus sensor monitoring");
        response.put("_links",      links);

        return response;
    }
}