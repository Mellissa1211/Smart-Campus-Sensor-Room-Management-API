/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.westminster.smart.campus.api.resources;

import com.westminster.smart.campus.api.models.Room;
import com.westminster.smart.campus.api.DataStore;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collection;

/**
 *
 * @author Mellissa
 */
@Path("/rooms")
public class RoomResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Room> getAllRooms() {
        // Access the rooms via the Singleton instance
        return DataStore.getInstance().rooms.values();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addRoom(Room room) {
        if (room.getId() == null || room.getName() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\": \"Room ID and Name are required\"}").build();
        }

        // Use getInstance() and access the 'rooms' map directly
        DataStore.getInstance().rooms.put(room.getId(), room);

        return Response.status(Response.Status.CREATED).entity(room).build();
    }
}
