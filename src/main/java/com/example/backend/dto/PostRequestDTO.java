package com.example.backend.dto;

public class PostRequestDTO {
    private String title;
    private String content;

    public PostRequestDTO() {
        // No-args constructor for deserialization
    }

    public PostRequestDTO(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
