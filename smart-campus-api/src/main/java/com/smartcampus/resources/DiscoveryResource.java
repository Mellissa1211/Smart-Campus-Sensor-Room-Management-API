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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscovery() {
        Map<String, Object> meta = new HashMap<>();
        meta.put("version", "v1");
        meta.put("contact", "admin@westminster.ac.uk");

        Map<String, String> links = new HashMap<>();
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        meta.put("links", links);

        return Response.ok(meta).build();
    }
}
