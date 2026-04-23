/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.models;

import java.util.ArrayList;
import java.util.List;

/**
 * TASK 2.1 - Room Model
 * Represents a physical campus room (lab, library, lecture hall etc.)
 * sensorIds list links this room to its installed sensors.
 * TASK 2.3 RULE: Room cannot be deleted if sensorIds is not empty.
 */
public class Room {
    private String id; // e.g., "LIB-301" 
    private String name; // e.g., "Library Quiet Study" 
    private int capacity; 
    private List<String> sensorIds = new ArrayList<>(); 
    public Room() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public List<String> getSensorIds() {
        return sensorIds;
    }

    public void setSensorIds(List<String> sensorIds) {
        this.sensorIds = sensorIds;
    }
    
    
}