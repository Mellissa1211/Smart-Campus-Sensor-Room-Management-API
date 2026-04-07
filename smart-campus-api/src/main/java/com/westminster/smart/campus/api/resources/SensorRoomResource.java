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
    private DataStore data = DataStore.getInstance();

    /** TASK 2.2: List all rooms */
    @GET
    public List<Room> getAll() { return new ArrayList<>(data.rooms.values()); }

    /** TASK 2.1: Add a new room */
    @POST
    public Response create(Room r) {
        data.rooms.put(r.getId(), r);
        return Response.status(Response.Status.CREATED).entity(r).build();
    }

    /** TASK 2.3: Delete room with safety validation */
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") String id) {
        Room r = data.rooms.get(id);
        
        if (r != null && !r.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Room cannot be deleted: it has active sensors.");
        }
        data.rooms.remove(id);
        
        return Response.noContent().build();
    }
}