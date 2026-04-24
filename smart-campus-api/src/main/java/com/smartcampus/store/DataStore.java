/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.store;

import com.smartcampus.models.Room;
import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorReading;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TASK 1.1: Core Data Structure Management Prevents zero mark by using HashMaps
 * instead of SQL. [cite: 206]
 */
public class DataStore {

    private static final Map<String, Room> rooms = new HashMap<>();
    private static final Map<String, Sensor> sensors = new HashMap<>();
    private static final Map<String, List<SensorReading>> readings = new HashMap<>();

    public static Map<String, Room> getRooms() {
        return rooms;
    }

    public static Map<String, Sensor> getSensors() {
        return sensors;
    }

    public static Map<String, List<SensorReading>> getReadings() {
        return readings;
    }
}
