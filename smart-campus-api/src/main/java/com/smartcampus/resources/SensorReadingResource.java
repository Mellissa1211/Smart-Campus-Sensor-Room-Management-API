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
import java.util.Map;
import java.util.UUID;

public class SensorReadingResource {

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    /**
     * TASK 4.2 & 5.3: Add a new reading with maintenance state validation.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        Sensor s = DataStore.sensors.get(sensorId);

        // Sensor must exist
        if (s == null) {
            throw new LinkedResourceNotFoundException("Sensor '" + sensorId + "' not found.");
        }

        // TASK 5.3: Block readings when sensor is in MAINTENANCE state
        if ("MAINTENANCE".equalsIgnoreCase(s.getStatus())) {
            throw new SensorUnavailableException(
                    "Reading rejected: Sensor '" + sensorId + "' is currently in MAINTENANCE mode.", sensorId);
        }

        // Auto-assign a unique ID if not provided
        if (reading.getId() == null || reading.getId().trim().isEmpty()) {
            reading.setId(UUID.randomUUID().toString());
        }

        // Set timestamp if not provided
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        // TASK 4.2 SIDE EFFECT: Update parent sensor's currentValue for data consistency
        s.setCurrentValue(reading.getValue());

        DataStore.readings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(reading);
        return Response.status(Response.Status.CREATED).entity(reading).build();
    }

    /**
     * TASK 4.2: Retrieve reading history for this sensor.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getHistory() {
        // Verify the sensor actually exists before returning history
        if (!DataStore.sensors.containsKey(sensorId)) {
            throw new LinkedResourceNotFoundException("Sensor '" + sensorId + "' not found.");
        }
        return Response.ok(DataStore.readings.getOrDefault(sensorId, new ArrayList<>())).build();
    }
}
