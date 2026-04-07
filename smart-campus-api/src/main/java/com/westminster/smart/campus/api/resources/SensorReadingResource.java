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

public class SensorReadingResource {
    private String sensorId;
    private DataStore data = DataStore.getInstance();

    public SensorReadingResource(String sensorId) { this.sensorId = sensorId; }

    /** TASK 4.2 & 4.3: Post readings and update parent sensor state */
    @POST
    public Response addRead(SensorReading r) {
        Sensor s = data.sensors.get(sensorId);
        // TASK 5.3: Handle Forbidden state
        if (s != null && "MAINTENANCE".equalsIgnoreCase(s.getStatus())) {
            throw new SensorUnavailableException("Sensor is in maintenance.");
        }
        if (s != null) s.setCurrentValue(r.getValue());
        data.readings.computeIfAbsent(sensorId, k -> new ArrayList<>()).add(r);
        return Response.status(Response.Status.CREATED).entity(r).build();
    }

    /** TASK 4.2: Retrieve reading history */
    @GET
    public List<SensorReading> get() {
        return data.readings.getOrDefault(sensorId, new ArrayList<>());
    }
}