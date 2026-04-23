/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resources;

import com.smartcampus.models.Room;
import com.smartcampus.store.DataStore;
import com.smartcampus.exceptions.RoomNotEmptyException;
import com.smartcampus.exceptions.LinkedResourceNotFoundException;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

/**
 *
 * @author Mellissa
 */
@Path("/rooms")
public class RoomResource {

    // GET a single room by ID
    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);
        if (room == null) {
            throw new LinkedResourceNotFoundException("Room not found: " + roomId);
        }
        return Response.ok(room).build();
    }

    // PUT - Full update of a room
    @PUT
    @Path("/{roomId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRoom(@PathParam("roomId") String roomId, Room updatedRoom) {
        Room existing = DataStore.rooms.get(roomId);
        if (existing == null) {
            throw new LinkedResourceNotFoundException("Room not found: " + roomId);
        }
        // Preserve existing sensor assignments during update
        updatedRoom.setId(roomId);
        updatedRoom.setSensorIds(existing.getSensorIds());
        DataStore.rooms.put(roomId, updatedRoom);
        return Response.ok(updatedRoom).build();
    }

    // PATCH - Partial update (e.g., update capacity only)
    @PATCH
    @Path("/{roomId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response patchRoom(@PathParam("roomId") String roomId, Map<String, Object> updates) {
        Room existing = DataStore.rooms.get(roomId);
        if (existing == null) {
            throw new LinkedResourceNotFoundException("Room not found: " + roomId);
        }
        if (updates.containsKey("name")) {
            existing.setName((String) updates.get("name"));
        }
        if (updates.containsKey("capacity")) {
            existing.setCapacity(((Number) updates.get("capacity")).intValue());
        }
        DataStore.rooms.put(roomId, existing);
        return Response.ok(existing).build();
    }

    // GET all sensors inside a specific room
    @GET
    @Path("/{roomId}/sensors")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorsInRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);
        if (room == null) {
            throw new LinkedResourceNotFoundException("Room not found: " + roomId);
        }
        List<com.smartcampus.models.Sensor> result = new ArrayList<>();
        for (String sensorId : room.getSensorIds()) {
            com.smartcampus.models.Sensor s = DataStore.sensors.get(sensorId);
            if (s != null) {
                result.add(s);
            }
        }
        return Response.ok(result).build();
    }
}
