/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resources;

import com.smartcampus.store.DataStore;
import com.smartcampus.models.Room;
import com.smartcampus.exceptions.RoomNotEmptyException;
import com.smartcampus.exceptions.LinkedResourceNotFoundException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

/**
 * Resource class managing Room entities. Implements Tasks 2.1, 2.2, and 5.1
 * safety constraints.
 */
@Path("/rooms")
public class RoomResource {

    /**
     * TASK 2.1: Retrieve all rooms from the in-memory DataStore.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllRooms() {
        return Response.ok(new ArrayList<>(DataStore.rooms.values())).build();
    }

    /**
     * TASK 2.1: Retrieve a single room by its ID. Returns 404 if room does not
     * exist.
     */
    @GET
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);

        if (room == null) {
            // Correct: throw with single message — this is a true 404-type scenario
            // mapped via LinkedResourceNotFoundException -> 422, but we return 404 directly
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("status", 404, "error", "Not Found",
                            "message", "Room not found: " + roomId))
                    .build();
        }
        return Response.ok(room).build();
    }

    /**
     * TASK 2.1: Create a new room with manual validation.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addRoom(Room room) {
        // Validation: Ensure ID is provided
        if (room.getId() == null || room.getId().trim().isEmpty()) {
            return Response.status(422)
                    .entity(Map.of("status", 422, "error", "Unprocessable Entity",
                            "message", "Room ID is required."))
                    .build();
        }

        // TASK 5.1: Conflict detection for duplicate IDs
        if (DataStore.rooms.containsKey(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(Map.of("status", 409, "error", "Conflict",
                            "message", "Room " + room.getId() + " already exists."))
                    .build();
        }

        // Initialize sensor list if null
        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<>());
        }

        DataStore.rooms.put(room.getId(), room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    /**
     * TASK 2.1: Full update of a room using PUT.
     */
    @PUT
    @Path("/{roomId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateRoom(@PathParam("roomId") String roomId, Room updatedRoom) {
        Room existing = DataStore.rooms.get(roomId);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("status", 404, "error", "Not Found",
                            "message", "Room not found: " + roomId))
                    .build();
        }

        updatedRoom.setId(roomId);
        // Preserve existing sensors during a full room update to prevent data loss
        updatedRoom.setSensorIds(existing.getSensorIds());

        DataStore.rooms.put(roomId, updatedRoom);
        return Response.ok(updatedRoom).build();
    }

    /**
     * TASK 2.1: Partial update of a room using PATCH.
     */
    @PATCH
    @Path("/{roomId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response patchRoom(@PathParam("roomId") String roomId, Map<String, Object> updates) {
        Room existing = DataStore.rooms.get(roomId);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("status", 404, "error", "Not Found",
                            "message", "Room not found: " + roomId))
                    .build();
        }

        if (updates.containsKey("name")) {
            existing.setName((String) updates.get("name"));
        }
        if (updates.containsKey("capacity")) {
            existing.setCapacity(((Number) updates.get("capacity")).intValue());
        }

        return Response.ok(existing).build();
    }

    /**
     * TASK 2.2 & 5.1: Safe deletion logic. Throws RoomNotEmptyException (->
     * 409) if sensors are still assigned.
     */
    @DELETE
    @Path("/{roomId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);

        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("status", 404, "error", "Not Found",
                            "message", "Room not found: " + roomId))
                    .build();
        }

        // TASK 5.1: Block deletion if sensors are still assigned
        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(
                    "Cannot delete room '" + roomId + "'; sensors are still assigned to it.", roomId);
        }

        DataStore.rooms.remove(roomId);
        return Response.noContent().build();
    }

    /**
     * TASK 4.1: Retrieve all sensors specifically linked to this room.
     */
    @GET
    @Path("/{roomId}/sensors")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSensorsInRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.rooms.get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(Map.of("status", 404, "error", "Not Found",
                            "message", "Room not found: " + roomId))
                    .build();
        }

        List<com.smartcampus.models.Sensor> sensors = new ArrayList<>();
        for (String sId : room.getSensorIds()) {
            com.smartcampus.models.Sensor sensor = DataStore.sensors.get(sId);
            if (sensor != null) {
                sensors.add(sensor);
            }
        }
        return Response.ok(sensors).build();
    }
}
