package com.example.websitebackend.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CsrfService {

    private static final String CSRF_TOKEN_KEY = "CSRF-TOKEN";

    public String getToken(HttpSession session) {
        return (String) session.getAttribute(CSRF_TOKEN_KEY);
    }

    public String generateToken(HttpSession session) {
        String csrfToken = UUID.randomUUID().toString();
        session.setAttribute(CSRF_TOKEN_KEY, csrfToken);
        return csrfToken;
    }

    public void validateCsrfToken(HttpSession session, String csrfToken) {
        // Check if the CSRF token is present in the session
        String storedToken = (String) session.getAttribute(CSRF_TOKEN_KEY);

        if (storedToken == null) {
            throw new CsrfException("CSRF token is missing in the session.");
        }

        // Validate that the CSRF token matches the one sent in the request
        if (csrfToken == null || csrfToken.trim().isEmpty() || !storedToken.equals(csrfToken)) {
            throw new CsrfException("Invalid CSRF token.");
        }
    }
}

