/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resources;

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
    public Response addReading(SensorReading reading) {
        Sensor s = DataStore.sensors.get(sensorId);

        if (s == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        if ("MAINTENANCE".equals(s.getStatus())) {
            throw new SensorUnavailableException("Sensor is in maintenance."); // [cite: 160]
        }

        // Side effect: Update current value [cite: 146]
        s.setCurrentValue(reading.getValue());

        DataStore.readings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);
        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
