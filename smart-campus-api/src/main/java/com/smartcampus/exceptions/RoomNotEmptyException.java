/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exceptions;

/**
 * TASK 5.1: Resource Conflict Scenario
 */
public class RoomNotEmptyException extends RuntimeException {

    private final String roomId;

    public RoomNotEmptyException(String message, String roomId) {
        super(message);
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }
}
