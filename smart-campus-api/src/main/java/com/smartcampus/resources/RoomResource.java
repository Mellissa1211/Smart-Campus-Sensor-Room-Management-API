/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resources;

import com.smartcampus.store.DataStore;
import com.smartcampus.models.Room;
import com.smartcampus.models.Sensor;
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

    // GET all rooms
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        return Response.ok(new ArrayList<>(DataStore.rooms.values())).build();
    }

    // GET single room by ID
    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);
        if (room == null) {
            throw new LinkedResourceNotFoundException("Room not found: " + roomId, "Room");
        }
        return Response.ok(room).build();
    }

    // POST - create room
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addRoom(Room room) {
        if (room.getId() == null || room.getId().trim().isEmpty()) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Unprocessable Entity");
            err.put("message", "Room ID is required.");
            return Response.status(422).entity(err).build();
        }
        if (DataStore.rooms.containsKey(room.getId())) {
            Map<String, String> err = new HashMap<>();
            err.put("error", "Conflict");
            err.put("message", "Room " + room.getId() + " already exists.");
            return Response.status(409).entity(err).build();
        }
        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<>());
        }
        DataStore.rooms.put(room.getId(), room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    // PUT - full update
    @PUT
    @Path("/{roomId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRoom(@PathParam("roomId") String roomId, Room updatedRoom) {
        Room existing = DataStore.rooms.get(roomId);
        if (existing == null) {
            throw new LinkedResourceNotFoundException("Room not found: " + roomId, "Room");
        }
        updatedRoom.setId(roomId);
        updatedRoom.setSensorIds(existing.getSensorIds());
        DataStore.rooms.put(roomId, updatedRoom);
        return Response.ok(updatedRoom).build();
    }

    // PATCH - partial update
    @PATCH
    @Path("/{roomId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response patchRoom(@PathParam("roomId") String roomId, Map<String, Object> updates) {
        Room existing = DataStore.rooms.get(roomId);
        if (existing == null) {
            throw new LinkedResourceNotFoundException("Room not found: " + roomId, "Room");
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

    // DELETE - remove room
    @DELETE
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);
        if (room == null) {
            throw new LinkedResourceNotFoundException("Room not found: " + roomId, "Room");
        }
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Cannot delete room with active sensors.", roomId);
        }
        DataStore.rooms.remove(roomId);
        return Response.noContent().build();
    }

    // GET all sensors inside a room
    @GET
    @Path("/{roomId}/sensors")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorsInRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);
        if (room == null) {
            throw new LinkedResourceNotFoundException("Room not found: " + roomId, "Room");
        }
        List<Sensor> result = new ArrayList<>();
        for (String sensorId : room.getSensorIds()) {
            Sensor s = DataStore.sensors.get(sensorId);
            if (s != null) {
                result.add(s);
            }
        }
        return Response.ok(result).build();
    }
}
