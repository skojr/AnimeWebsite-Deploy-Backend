package com.example.backend.dto;

import com.example.backend.model.User;

public class UserResponseDTO {
    private String username;
    private String message;

    public UserResponseDTO(User savedUser, String message) {
        this.username = savedUser.getUsername();
        this.message = message;
    }

    public UserResponseDTO(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
