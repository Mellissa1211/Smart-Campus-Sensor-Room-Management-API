/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.westminster.smart.campus.api;

import com.westminster.smart.campus.api.models.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TASK 8: Database Constraint
 * Using ConcurrentHashMap instead of SQL/NoSQL to store data in-memory.
 * This class follows the Singleton pattern to ensure data persists during the app lifecycle.
 */
public class DataStore {
    private static DataStore instance;
    
    public Map<String, Room> rooms = new ConcurrentHashMap<>();
    public Map<String, Sensor> sensors = new ConcurrentHashMap<>();
    public Map<String, List<SensorReading>> readings = new ConcurrentHashMap<>();

    private DataStore() {}

    public static synchronized DataStore getInstance() {
        if (instance == null) instance = new DataStore();
        return instance;
    }
}