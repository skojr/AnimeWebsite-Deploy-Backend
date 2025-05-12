package com.example.backend.dto;

import java.time.LocalDateTime;

public class PostResponseDTO {
    private String message;
    private String username;
    private LocalDateTime createdAt;
    private String content;
    private String title;
    private Long id;

    public PostResponseDTO(String message, String username, Long id, LocalDateTime createdAt, String content, String title) {
        this.message = message;
        this.username = username;
        this.createdAt = createdAt;
        this.content = content;
        this.title = title;
        this.id = id;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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
