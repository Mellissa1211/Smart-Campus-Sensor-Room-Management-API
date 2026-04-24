/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resources;

import com.smartcampus.exceptions.LinkedResourceNotFoundException;
import com.smartcampus.exceptions.SensorUnavailableException;
import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorReading;
import com.smartcampus.store.DataStore;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;

public class SensorReadingResource {

    private String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    /**
     * TASK 4.2 & 5.3: Add reading with maintenance check. Includes side-effect
     * to update the parent sensor's current value.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        Sensor s = DataStore.sensors.get(sensorId);
        if (s == null) {
            throw new LinkedResourceNotFoundException("Sensor not found.");
        }

        // Logic for Task 5.3 (403 Forbidden Mapper)
        if ("MAINTENANCE".equals(s.getStatus())) {
            throw new SensorUnavailableException("Sensor is currently in maintenance.", sensorId);
        }

        // Update parent sensor value (Task 4.2 Side Effect)
        s.setCurrentValue(reading.getValue());

        DataStore.readings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);
        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
