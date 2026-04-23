package com.smartcampus.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

/**
 * TASK 1.2: Discovery Endpoint Providing API metadata and HATEOAS-style links
 * to navigate the API.
 */
@Path("/")
public class DiscoveryResource {

    @Context
    UriInfo uriInfo;  // ← automatically knows the full base URL

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscovery() {
        String base = uriInfo.getBaseUri().toString(); // e.g. http://localhost:8080/smart-campus-api/api/v1/

        Map<String, Object> meta = new HashMap<>();
        meta.put("version", "v1");
        meta.put("description", "Smart Campus Sensor Management API");
        meta.put("contact", "admin@smartcampus.ac.uk");

        Map<String, String> links = new HashMap<>();
        links.put("rooms", base + "rooms");
        links.put("sensors", base + "sensors");
        meta.put("links", links);

        return Response.ok(meta).build();
    }
}
