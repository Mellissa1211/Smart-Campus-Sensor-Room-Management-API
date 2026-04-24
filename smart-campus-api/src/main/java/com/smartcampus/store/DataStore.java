/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.store;

import com.smartcampus.models.*;
import java.util.*;

public class DataStore {

    // These MUST be public static to fix the errors in your screenshots
    public static Map<String, Room> rooms = new HashMap<>();
    public static Map<String, Sensor> sensors = new HashMap<>();
    public static Map<String, List<SensorReading>> readings = new HashMap<>();
}
