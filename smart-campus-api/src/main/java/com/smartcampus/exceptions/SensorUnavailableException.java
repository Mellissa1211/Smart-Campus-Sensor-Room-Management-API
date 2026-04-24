/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exceptions;

/**
 * TASK 5.3: State Constraint Scenario (Maintenance Mode)
 */
public class SensorUnavailableException extends RuntimeException {

    private final String sensorId;

    public SensorUnavailableException(String message, String sensorId) {
        super(message);
        this.sensorId = sensorId;
    }

    public String getSensorId() {
        return sensorId;
    }
}
