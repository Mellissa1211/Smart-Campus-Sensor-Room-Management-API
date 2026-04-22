/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.westminster.smart.campus.api.resources;

import com.westminster.smart.campus.api.DataStore;
import com.westminster.smart.campus.api.models.Room;
import com.westminster.smart.campus.api.exceptions.RoomNotEmptyException;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.*;

@Path("rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorRoomResource {
    
    private final DataStore dataStore = DataStore.getInstance();

    // TASK 2.1 - List all rooms
    @GET
    public List<Room> getAllRooms() {
        return new ArrayList<>(dataStore.rooms.values());
    }

    // TASK 2.1 - Create a new room, returns 201 Created
    @POST
    public Response createRoom(Room room) {
        dataStore.rooms.put(room.getId(), room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    // TASK 2.1 - Get a specific room by ID
    @GET
    @Path("{roomId}")
    public Response getRoomById(@PathParam("roomId") String roomId) {
        Room room = dataStore.rooms.get(roomId);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Room not found: " + roomId + "\"}").build();
        }
        return Response.ok(room).build();
    }

    // TASK 2.3 - Delete room with safety check
    // Throws RoomNotEmptyException (→ 409) if the room still has sensors
    @DELETE
    @Path("{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = dataStore.rooms.get(roomId);

        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\": \"Room not found: " + roomId + "\"}").build();
        }

        // TASK 5.1 safety check — block deletion if sensors are still assigned
        if (!room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(
                "Cannot delete room '" + roomId + "'. It still has "
                + room.getSensorIds().size() + " active sensor(s). "
                + "Deregister all sensors first."
            );
        }

        dataStore.rooms.remove(roomId);
        return Response.noContent().build(); // 204
    }
}