/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.store;

import com.smartcampus.models.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Part 1.1 - Data Management - Use data structures like HashMap/ArrayList (No
 * SQL allowed)
 */
public class DataStore {

    public static Map<String, Room> rooms = new ConcurrentHashMap<>();
    public static Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    // Key is SensorID, Value is List of Readings
    public static Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();
}
