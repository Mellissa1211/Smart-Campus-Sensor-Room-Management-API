/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

@Path("/")
public class DiscoveryResource {

    @Context
    private UriInfo uriInfo;

    /**
     * TASK 1.2: Discovery Endpoint providing API metadata and navigation links.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscovery() {
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
