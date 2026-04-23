/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.exceptions;

/**
 * TASK 5.1: Custom Conflict Exception (409)
 */
public class RoomNotEmptyException extends RuntimeException {

    private final String roomId;

    public RoomNotEmptyException(String message) {
        super(message);
        this.roomId = null;
    }

    public RoomNotEmptyException(String message, String roomId) {
        super(message);
        this.roomId = roomId;
    }

    public String getRoomId() {
        return roomId;
    }
}
