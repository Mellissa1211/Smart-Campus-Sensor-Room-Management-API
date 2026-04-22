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

/**
 * TASK 3 - Sensor Operations & Linking
 */

@Path("sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final DataStore dataStore = DataStore.getInstance();

    // TASK 3.1 - Register a sensor, validate roomId exists first
    @POST
    public Response registerSensor(Sensor sensor) {
        // TASK 5.2 - If roomId doesn't exist, throw 422
        if (sensor.getRoomId() == null || !dataStore.rooms.containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException(
                "Cannot register sensor. Room ID '"
                + sensor.getRoomId() + "' does not exist. Create the room first."
            );
        }
        dataStore.sensors.put(sensor.getId(), sensor);
        // Side effect: add sensor ID to the parent room's sensorIds list
        dataStore.rooms.get(sensor.getRoomId()).getSensorIds().add(sensor.getId());
        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    // TASK 3.2 - Get all sensors, with optional ?type= filter (case-insensitive)
    @GET
    public List<Sensor> getSensors(@QueryParam("type") String type) {
        if (type == null || type.trim().isEmpty()) {
            return new ArrayList<>(dataStore.sensors.values());
        }
        return dataStore.sensors.values().stream()
                .filter(s -> s.getType() != null && s.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    // Get a single sensor by ID
    @GET
    @Path("{sensorId}")
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = dataStore.sensors.get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Sensor not found: " + sensorId + "\"}").build();
        }
        return Response.ok(sensor).build();
    }

    // Update a sensor — use this to change status to MAINTENANCE/ACTIVE/OFFLINE
    @PUT
    @Path("{sensorId}")
    public Response updateSensor(@PathParam("sensorId") String sensorId, Sensor updated) {
        Sensor existing = dataStore.sensors.get(sensorId);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Sensor not found: " + sensorId + "\"}").build();
        }
        existing.setType(updated.getType());
        existing.setStatus(updated.getStatus());
        existing.setCurrentValue(updated.getCurrentValue());
        return Response.ok(existing).build();
    }

    // TASK 4.1 - Sub-resource locator: delegates /sensors/{id}/read to SensorReadingResource
    // No @GET/@POST here — just @Path makes it a locator, not a regular endpoint
    @Path("{sensorId}/read")
    public SensorReadingResource getReadingSubResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}