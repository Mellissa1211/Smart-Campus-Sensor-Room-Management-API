/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resources;

import com.smartcampus.store.DataStore;
import com.smartcampus.models.Sensor;
import com.smartcampus.models.Room;
import com.smartcampus.exceptions.LinkedResourceNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;
import java.util.stream.Collectors;

@Path("/sensors")
public class SensorResource {

    /**
     * TASK 3.2: Filtered retrieval of sensors by type using @QueryParam.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensors(@QueryParam("type") String type) {
        if (type != null && !type.isEmpty()) {
            List<Sensor> filtered = DataStore.sensors.values().stream()
                    .filter(s -> s.getType() != null && s.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
            return Response.ok(filtered).build();
        }
        return Response.ok(new ArrayList<>(DataStore.sensors.values())).build();
    }

    /**
     * TASK 3.1: Retrieve a single sensor by ID.
     */
    @GET
    @Path("/{sensorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorById(@PathParam("sensorId") String sensorId) {
        Sensor s = DataStore.sensors.get(sensorId);
        if (s == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("status", 404, "error", "Not Found",
                            "message", "Sensor not found: " + sensorId))
                    .build();
        }
        return Response.ok(s).build();
    }

    /**
     * TASK 3.1 & 5.2: Registration with Room Integrity Check. Ensures the
     * roomId in the JSON actually exists in the DataStore.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSensor(Sensor sensor) {
        // Basic validation
        if (sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            return Response.status(422)
                    .entity(Map.of("status", 422, "error", "Unprocessable Entity",
                            "message", "Sensor ID is required."))
                    .build();
        }

        // Conflict check
        if (DataStore.sensors.containsKey(sensor.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("status", 409, "error", "Conflict",
                            "message", "Sensor " + sensor.getId() + " already exists."))
                    .build();
        }

        // TASK 5.2: Dependency Validation — roomId must exist
        Room targetRoom = DataStore.rooms.get(sensor.getRoomId());
        if (targetRoom == null) {
            throw new LinkedResourceNotFoundException(
                    "Validation Failed: Room '" + sensor.getRoomId() + "' does not exist.");
        }

        // Add to global store
        DataStore.sensors.put(sensor.getId(), sensor);

        // Maintain the bi-directional link (required for Task 5.1 room deletion guard)
        if (targetRoom.getSensorIds() == null) {
            targetRoom.setSensorIds(new ArrayList<>());
        }
        if (!targetRoom.getSensorIds().contains(sensor.getId())) {
            targetRoom.getSensorIds().add(sensor.getId());
        }

        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    /**
     * Update a sensor's details using PUT.
     */
    @PUT
    @Path("/{sensorId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSensor(@PathParam("sensorId") String sensorId, Sensor updatedSensor) {
        Sensor existing = DataStore.sensors.get(sensorId);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("status", 404, "error", "Not Found",
                            "message", "Sensor not found: " + sensorId))
                    .build();
        }
        updatedSensor.setId(sensorId);
        DataStore.sensors.put(sensorId, updatedSensor);
        return Response.ok(updatedSensor).build();
    }

    /**
     * Delete a sensor and remove it from its parent room's list.
     */
    @DELETE
    @Path("/{sensorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.sensors.get(sensorId);
        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("status", 404, "error", "Not Found",
                            "message", "Sensor not found: " + sensorId))
                    .build();
        }

        // Remove from parent room's sensorIds list to maintain integrity
        Room parentRoom = DataStore.rooms.get(sensor.getRoomId());
        if (parentRoom != null && parentRoom.getSensorIds() != null) {
            parentRoom.getSensorIds().remove(sensorId);
        }

        DataStore.sensors.remove(sensorId);
        return Response.noContent().build();
    }

    /**
     * TASK 4.1: Sub-resource locator for sensor readings. Delegates handling to
     * SensorReadingResource.
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadings(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
