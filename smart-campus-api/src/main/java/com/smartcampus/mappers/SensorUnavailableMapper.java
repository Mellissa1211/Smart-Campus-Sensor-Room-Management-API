/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.mappers;

import com.smartcampus.exceptions.SensorUnavailableException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * TASK 5.3: Maps SensorUnavailableException to 403 Forbidden.
 */
@Provider
public class SensorUnavailableMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", 403);
        error.put("error", "Forbidden");
        error.put("message", ex.getMessage());
        if (ex.getSensorId() != null) {
            error.put("sensorId", ex.getSensorId());
        }
        return Response.status(Response.Status.FORBIDDEN).entity(error).build();
    }
}
