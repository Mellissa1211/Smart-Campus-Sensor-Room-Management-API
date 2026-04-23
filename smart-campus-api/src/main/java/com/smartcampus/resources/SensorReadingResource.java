/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resources;

import com.smartcampus.exceptions.LinkedResourceNotFoundException;
import com.smartcampus.models.SensorReading;
import com.smartcampus.models.Sensor;
import com.smartcampus.store.DataStore;
import com.smartcampus.exceptions.SensorUnavailableException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

/**
 * TASK 4.2 - Historical Data Management (Sub-Resource)
 *
 */
public class SensorReadingResource {

    private String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)  // ← ADDED
    public Response addReading(SensorReading reading) {
        Sensor s = DataStore.sensors.get(sensorId);
        if (s == null) {
            throw new LinkedResourceNotFoundException("Sensor not found: " + sensorId, "Sensor");
        }
        if ("MAINTENANCE".equals(s.getStatus()) || "OFFLINE".equals(s.getStatus())) {
            throw new SensorUnavailableException("Sensor is unavailable.", sensorId, s.getStatus());
        }
        if (reading.getId() == null) {
            reading.setId(java.util.UUID.randomUUID().toString());
        }
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }
        s.setCurrentValue(reading.getValue());
        DataStore.readings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);
        return Response.status(Response.Status.CREATED).entity(reading).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)  // ← GET all readings for a sensor
    public Response getReadings() {
        List<SensorReading> list = DataStore.readings.getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(list).build();
    }
}
