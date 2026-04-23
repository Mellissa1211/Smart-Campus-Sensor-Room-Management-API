/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exceptions;

/**
 * TASK 5.3: Custom Forbidden Exception (403)
 */
public class SensorUnavailableException extends RuntimeException {

    private final String sensorId;
    private final String sensorStatus;

    public SensorUnavailableException(String message) {
        super(message);
        this.sensorId = null;
        this.sensorStatus = null;
    }

    public SensorUnavailableException(String message, String sensorId, String sensorStatus) {
        super(message);
        this.sensorId = sensorId;
        this.sensorStatus = sensorStatus;
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getSensorStatus() {
        return sensorStatus;
    }
}
