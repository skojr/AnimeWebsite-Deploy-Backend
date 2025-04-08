package com.example.websitebackend.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Setter
@Getter
public class AuthenticationRequest {
    // Getters and Setters
    private String email;
    private String password;

    // Default constructor
    public AuthenticationRequest() {
    }

    // Parameterized constructor
    public AuthenticationRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public HttpSession getSession(HttpServletRequest request) {
        return request.getSession(true);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AuthenticationRequest that = (AuthenticationRequest) o;
        return Objects.equals(email, that.email) &&
                Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, password);
    }

    @Override
    public String toString() {
        return "AuthenticationRequest{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

