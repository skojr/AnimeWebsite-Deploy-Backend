package com.example.backend.dto;

import com.example.backend.model.User;

public class GetUserResponseDTO {
    private String username;
    private String message;

    public GetUserResponseDTO(User user, String message) {
        this.username = user.getUsername();
        this.message = message;
    }

    public GetUserResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
