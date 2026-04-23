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

    // GET all sensors with optional type filter
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

    // GET single sensor
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

    // POST - create sensor
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSensor(Sensor sensor) {
        // 422 - missing ID
        if (sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Unprocessable Entity");
            err.put("message", "Sensor ID is required.");
            return Response.status(422).entity(err).build();
        }
        // 422 - missing type
        if (sensor.getType() == null || sensor.getType().trim().isEmpty()) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Unprocessable Entity");
            err.put("message", "Sensor type is required.");
            return Response.status(422).entity(err).build();
        }
        // 422 - invalid status
        if (sensor.getStatus() != null
                && !sensor.getStatus().equals("ACTIVE")
                && !sensor.getStatus().equals("MAINTENANCE")
                && !sensor.getStatus().equals("OFFLINE")) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Unprocessable Entity");
            err.put("message", "Status must be ACTIVE, MAINTENANCE or OFFLINE.");
            return Response.status(422).entity(err).build();
        }
        // default status to ACTIVE if not provided
        if (sensor.getStatus() == null) {
            sensor.setStatus("ACTIVE");
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
        DataStore.rooms.get(sensor.getRoomId()).getSensorIds().add(sensor.getId());
        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    // PATCH - partial update (status, value, type)
    @PATCH
    @Path("/{sensorId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response patchSensor(@PathParam("sensorId") String sensorId, Map<String, Object> updates) {
        Sensor sensor = DataStore.sensors.get(sensorId);
        if (sensor == null) {
            throw new LinkedResourceNotFoundException("Sensor not found: " + sensorId, "Sensor");
        }
        if (updates.containsKey("status")) {
            String newStatus = (String) updates.get("status");
            if (!newStatus.equals("ACTIVE")
                    && !newStatus.equals("MAINTENANCE")
                    && !newStatus.equals("OFFLINE")) {
                Map<String, String> err = new HashMap<>();
                err.put("error", "Unprocessable Entity");
                err.put("message", "Status must be ACTIVE, MAINTENANCE or OFFLINE.");
                return Response.status(422).entity(err).build();
            }
            sensor.setStatus(newStatus);
        }
        if (updates.containsKey("currentValue")) {
            sensor.setCurrentValue(((Number) updates.get("currentValue")).doubleValue());
        }
        if (updates.containsKey("type")) {
            sensor.setType((String) updates.get("type"));
        }
        DataStore.sensors.put(sensorId, sensor);
        return Response.ok(sensor).build();
    }

    // DELETE - remove sensor and unlink from room
    @DELETE
    @Path("/{sensorId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.sensors.get(sensorId);
        if (sensor == null) {
            throw new LinkedResourceNotFoundException("Sensor not found: " + sensorId, "Sensor");
        }
        // unlink from room
        String roomId = sensor.getRoomId();
        if (roomId != null && DataStore.rooms.containsKey(roomId)) {
            DataStore.rooms.get(roomId).getSensorIds().remove(sensorId);
        }
        DataStore.sensors.remove(sensorId);
        DataStore.readings.remove(sensorId);
        return Response.noContent().build();
    }

    // Sub-resource locator for readings
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadings(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
