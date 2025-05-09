package com.example.backend.dto;

import com.example.backend.model.User;

public class AuthResponseDTO {
    private String username;
    private String email;
    private Long id;

    public AuthResponseDTO(User user) {
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.id = user.getId();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
