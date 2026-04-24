/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exceptions;

/**
 * TASK 5.2: Dependency Validation Scenario
 */
public class LinkedResourceNotFoundException extends RuntimeException {

    // Single-arg constructor (used by SensorResource, SensorReadingResource)
    public LinkedResourceNotFoundException(String message) {
        super(message);
    }

    // Two-arg constructor (used by RoomResource — passes resourceType as second arg)
    public LinkedResourceNotFoundException(String message, String resourceType) {
        super(message + " [Resource type: " + resourceType + "]");
    }
}
