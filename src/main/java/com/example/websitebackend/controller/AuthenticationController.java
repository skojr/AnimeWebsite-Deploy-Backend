package com.example.websitebackend.controller;

import com.example.websitebackend.auth.AuthenticationRequest;
import com.example.websitebackend.auth.AuthenticationResponse;
import com.example.websitebackend.auth.RegisterRequest;
import com.example.websitebackend.service.AuthenticationService;
import com.example.websitebackend.service.CsrfService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users/auth")
public class AuthenticationController {

    private final AuthenticationService service;

    public AuthenticationController(final AuthenticationService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request, HttpServletResponse response, HttpSession session) {
        return this.service.register(request, response, session);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthenticationRequest request, HttpServletResponse response, HttpSession session) {
        return this.service.authenticate(request, response, session);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Expire the JWT cookie
        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0); // Expire immediately
        response.addCookie(jwtCookie);

        // Expire the CSRF cookie if you're using one
        Cookie csrfCookie = new Cookie("csrfToken", null);
        csrfCookie.setHttpOnly(false);
        csrfCookie.setSecure(true);
        csrfCookie.setPath("/");
        csrfCookie.setMaxAge(0);
        response.addCookie(csrfCookie);

        return ResponseEntity.ok("Logged out successfully.");
    }

}

