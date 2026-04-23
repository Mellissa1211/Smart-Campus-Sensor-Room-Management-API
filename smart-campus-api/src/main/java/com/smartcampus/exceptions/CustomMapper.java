/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

/**
 * TASK 5: Advanced Error Handling (Exception Mapping) Ensures "leak-proof" API
 * behavior by mapping exceptions to JSON responses.
 */
// ── 409 Conflict ──────────────────────────────────────────────
@Provider
public class CustomMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", 409);
        error.put("error", "Conflict");
        error.put("message", ex.getMessage());
        if (ex.getRoomId() != null) {
            error.put("roomId", ex.getRoomId());
        }
        return Response.status(Response.Status.CONFLICT).entity(error).build();
    }
}

// ── 404 Not Found ─────────────────────────────────────────────
@Provider
class NotFoundMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", 404);
        error.put("error", "Not Found");
        error.put("message", ex.getMessage());
        error.put("resourceType", ex.getResourceType());
        return Response.status(Response.Status.NOT_FOUND).entity(error).build();
    }
}

// ── 422 Unprocessable Entity ──────────────────────────────────
@Provider
class SensorUnavailableMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", 422);
        error.put("error", "Unprocessable Entity");
        error.put("message", ex.getMessage());
        if (ex.getSensorId() != null) {
            error.put("sensorId", ex.getSensorId());
        }
        if (ex.getSensorStatus() != null) {
            error.put("currentStatus", ex.getSensorStatus());
        }
        return Response.status(422).entity(error).build();
    }
}

// ── 500 Internal Server Error ─────────────────────────────────
@Provider
class GenericExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("status", 500);
        error.put("error", "Internal Server Error");
        error.put("message", "An unexpected error occurred.");
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
    }
}
