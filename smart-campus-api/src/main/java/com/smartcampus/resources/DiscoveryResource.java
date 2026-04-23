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
    UriInfo uriInfo;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscovery() {

        // gets just: /smart-campus-api/api/v1/
        String base = uriInfo.getBaseUri().getPath();

        Map<String, Object> meta = new HashMap<>();
        meta.put("version", "1.0");
        meta.put("description", "Smart Campus Sensor Management API");

        Map<String, String> links = new HashMap<>();
        links.put("rooms", base + "rooms");
        links.put("sensors", base + "sensors");
        meta.put("links", links);

        return Response.ok(meta).build();
    }
}
