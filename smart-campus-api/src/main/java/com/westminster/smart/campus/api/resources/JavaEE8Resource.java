package com.westminster.smart.campus.api.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * TASK 1.2: Discovery Endpoint
 * Providing API metadata and HATEOAS-style links to navigate the API.
 */
@Path("/")
public class JavaEE8Resource {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getDiscovery() {
        Map<String, Object> meta = new HashMap<>();
        meta.put("api_name", "Smart Campus API");
        meta.put("version", "1.0");
        
        Map<String, String> links = new HashMap<>();
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        meta.put("_links", links);
        
        return meta;
    }
}