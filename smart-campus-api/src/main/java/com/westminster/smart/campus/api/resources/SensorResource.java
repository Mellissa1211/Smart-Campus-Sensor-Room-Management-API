/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.westminster.smart.campus.api.resources;

import com.westminster.smart.campus.api.DataStore;
import com.westminster.smart.campus.api.models.Sensor;
import com.westminster.smart.campus.api.exceptions.LinkedResourceNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.stream.Collectors;

@Path("sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {
    private DataStore data = DataStore.getInstance();

    /** TASK 3.1: Register a sensor and link to a room */
    @POST
    public Response register(Sensor s) {
        if (!data.rooms.containsKey(s.getRoomId())) {
            throw new LinkedResourceNotFoundException("Room ID " + s.getRoomId() + " not found.");
        }
        data.sensors.put(s.getId(), s);
        data.rooms.get(s.getRoomId()).getSensorIds().add(s.getId());
        return Response.status(Response.Status.CREATED).entity(s).build();
    }

    /** TASK 3.2: Filter sensors by type using Query Parameters */
    @GET
    public List<Sensor> get(@QueryParam("type") String type) {
        if (type == null) return new ArrayList<>(data.sensors.values());
        return data.sensors.values().stream()
            .filter(s -> s.getType().equalsIgnoreCase(type))
            .collect(Collectors.toList());
    }

    /** TASK 4.1: Sub-resource locator for nested readings */
    @Path("{id}/read")
    public SensorReadingResource getReadings(@PathParam("id") String id) {
        return new SensorReadingResource(id);
    }
}