/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resources;

import com.smartcampus.store.DataStore;
import com.smartcampus.models.Sensor;
import com.smartcampus.exceptions.LinkedResourceNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * TASK 3 - Sensor Operations & Linking
 */
@Path("/sensors")
public class SensorResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensors(@QueryParam("type") String type) {
        List<Sensor> result;
        if (type != null) {
            result = DataStore.sensors.values().stream()
                    .filter(s -> s.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
        } else {
            result = new ArrayList<>(DataStore.sensors.values());
        }
        return Response.ok(result).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)  // ← THIS IS THE FIX
    public Response addSensor(Sensor sensor) {
        // 422 - missing fields
        if (sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Unprocessable Entity");
            err.put("message", "Sensor ID is required.");
            return Response.status(422).entity(err).build();
        }
        // 404 - room must exist
        if (!DataStore.rooms.containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException(
                    "Room not found: " + sensor.getRoomId(), "Room"
            );
        }
        // 409 - duplicate sensor
        if (DataStore.sensors.containsKey(sensor.getId())) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Conflict");
            err.put("message", "Sensor " + sensor.getId() + " already exists.");
            return Response.status(409).entity(err).build();
        }

        DataStore.sensors.put(sensor.getId(), sensor);
        // link sensor to room
        DataStore.rooms.get(sensor.getRoomId()).getSensorIds().add(sensor.getId());

        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    @GET
    @Path("/{sensorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.sensors.get(sensorId);
        if (sensor == null) {
            throw new LinkedResourceNotFoundException("Sensor not found: " + sensorId, "Sensor");
        }
        return Response.ok(sensor).build();
    }

    // Sub-resource locator for readings
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadings(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
