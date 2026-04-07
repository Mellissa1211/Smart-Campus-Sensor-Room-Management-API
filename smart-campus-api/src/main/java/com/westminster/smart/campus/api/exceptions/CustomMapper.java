/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.westminster.smart.campus.api.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.*;

/**
 * TASK 5: Advanced Error Handling (Exception Mapping)
 * Ensures "leak-proof" API behavior by mapping exceptions to JSON responses.
 */
@Provider
public class CustomMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable t) {
        if (t instanceof RoomNotEmptyException) 
            return Response.status(409).entity("{\"error\":\"" + t.getMessage() + "\"}").build();
        
        if (t instanceof LinkedResourceNotFoundException) 
            return Response.status(422).entity("{\"error\":\"" + t.getMessage() + "\"}").build();
        
        if (t instanceof SensorUnavailableException) 
            return Response.status(403).entity("{\"error\":\"" + t.getMessage() + "\"}").build();
            
        // TASK 5.4: Global Safety Net (500)
        return Response.status(500).entity("{\"error\":\"Internal Server Error\"}").build();
    }
}