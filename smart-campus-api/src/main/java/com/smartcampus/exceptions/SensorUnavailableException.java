/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exceptions;

/**
 * TASK 5.3: Custom Forbidden Exception (403)
 */
public class SensorUnavailableException extends RuntimeException {
    public SensorUnavailableException(String m) { super(m); }
}