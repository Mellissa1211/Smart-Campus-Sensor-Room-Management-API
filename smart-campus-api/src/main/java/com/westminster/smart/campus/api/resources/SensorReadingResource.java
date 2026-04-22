/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.westminster.smart.campus.api.resources;

import com.westminster.smart.campus.api.DataStore;
import com.westminster.smart.campus.api.models.*;
import com.westminster.smart.campus.api.exceptions.SensorUnavailableException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

/**
 * TASK 4.2 - Historical Data Management (Sub-Resource)
 * 
 */

public class SensorReadingResource {

    private final String sensorId;
    private final DataStore dataStore = DataStore.getInstance();

    // Constructor called by the sub-resource locator with the sensorId from the URL
    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    // TASK 4.2 - Post a new reading
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        Sensor sensor = dataStore.sensors.get(sensorId);

        // TASK 5.3 - Block readings if sensor is in MAINTENANCE → throws 403
        if (sensor != null && "MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                "Sensor '" + sensorId + "' is in MAINTENANCE mode. "
                + "Cannot accept new readings."
            );
        }

        // TASK 4.2 SIDE EFFECT - update parent sensor's currentValue
        if (sensor != null) {
            sensor.setCurrentValue(reading.getValue());
        }

        // Save the reading to the history list for this sensor
        dataStore.readings
                .computeIfAbsent(sensorId, k -> new ArrayList<>())
                .add(reading);

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }

    // TASK 4.2 - Get full reading history for this sensor
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SensorReading> getReadingHistory() {
        // Returns empty list (not 404) if sensor exists but has no readings yet
        return dataStore.readings.getOrDefault(sensorId, new ArrayList<>());
    }
}