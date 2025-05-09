package com.example.backend.dto;

public class ErrorResponse {
    private String message;
    private String timestamp;
    private int status;

    public ErrorResponse(String message, int status) {
        this.message = message;
        this.status = status;
        this.timestamp = java.time.Instant.now().toString();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
