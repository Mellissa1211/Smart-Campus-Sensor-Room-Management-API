/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resources;

import com.smartcampus.store.DataStore;
import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorReading;
import com.smartcampus.exceptions.LinkedResourceNotFoundException;
import com.smartcampus.exceptions.SensorUnavailableException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.ArrayList;

public class SensorReadingResource {

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    /**
     * TASK 4.2 & 5.3: Add reading with maintenance check.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        Sensor s = DataStore.sensors.get(sensorId);

        if (s == null) {
            throw new LinkedResourceNotFoundException("Sensor " + sensorId + " not found.");
        }

        if ("MAINTENANCE".equals(s.getStatus())) {
            throw new SensorUnavailableException("Reading rejected: Sensor is undergoing maintenance.", sensorId);
        }

        // TASK 4.2: SIDE EFFECT - Update parent sensor current value
        s.setCurrentValue(reading.getValue());

        DataStore.readings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);
        return Response.status(Response.Status.CREATED).entity(reading).build();
    }

    /**
     * TASK 4.2: Retrieve history.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistory() {
        return Response.ok(DataStore.readings.getOrDefault(sensorId, new ArrayList<>())).build();
    }
}
