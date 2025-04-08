package com.example.websitebackend.controller;

import com.example.websitebackend.auth.AuthenticationRequest;
import com.example.websitebackend.auth.AuthenticationResponse;
import com.example.websitebackend.auth.RegisterRequest;
import com.example.websitebackend.auth.UserResponse;
import com.example.websitebackend.model.CustomUser;
import com.example.websitebackend.repository.UserRepository;
import com.example.websitebackend.service.AuthenticationService;
import com.example.websitebackend.service.CsrfService;
import com.example.websitebackend.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping({"/api/users/auth"})
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping({"/register"})
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(this.service.register(request));
    }

    @PostMapping({"/authenticate"})
    public ResponseEntity<AuthenticationResponse> register(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(this.service.authenticate(request));
    }

    public AuthenticationController(final AuthenticationService service) {
        this.service = service;
    }
}

