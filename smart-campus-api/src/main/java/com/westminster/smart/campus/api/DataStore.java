/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.westminster.smart.campus.api;

import com.westminster.smart.campus.api.models.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Part 1.1 - Data Management - Use data structures like HashMap/ArrayList (No SQL allowed)
 */
public class DataStore {
    private static DataStore instance = new DataStore();
    
    // Task 1.1: Architectural decision to use ConcurrentHashMap for race-condition prevention
    public Map<String, Room> rooms = new ConcurrentHashMap<>();
    
    public Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    
    public Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    private DataStore() {}
    public static DataStore getInstance() { return instance; }
}