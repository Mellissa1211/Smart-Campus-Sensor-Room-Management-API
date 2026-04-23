/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exceptions;

/**
 * TASK 5.2: Custom Unprocessable Entity Exception (422)
 */
public class LinkedResourceNotFoundException extends RuntimeException {

    private final String resourceType;

    public LinkedResourceNotFoundException(String message) {
        super(message);
        this.resourceType = "Unknown";
    }

    public LinkedResourceNotFoundException(String message, String resourceType) {
        super(message);
        this.resourceType = resourceType;
    }

    public String getResourceType() {
        return resourceType;
    }
}
