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

@Path("/sensors")
public class SensorResource {

    /**
     * TASK 3.2: Filtered search by sensor type.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensors(@QueryParam("type") String type) {
        if (type != null) {
            List<Sensor> filtered = DataStore.sensors.values().stream()
                    .filter(s -> s.getType().equalsIgnoreCase(type))
                    .collect(Collectors.toList());
            return Response.ok(filtered).build();
        }
        return Response.ok(new ArrayList<>(DataStore.sensors.values())).build();
    }

    /**
     * TASK 3.1 & 5.2: Integrity check for Room existence. Throws
     * LinkedResourceNotFoundException for the 422 Mapper.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSensor(Sensor sensor) {
        if (!DataStore.rooms.containsKey(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException("Target Room ID does not exist.");
        }

        DataStore.sensors.put(sensor.getId(), sensor);
        DataStore.rooms.get(sensor.getRoomId()).getSensorIds().add(sensor.getId());
        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    /**
     * TASK 4.1: Sub-resource locator for historical data.
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadings(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
