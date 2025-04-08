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
@RequestMapping("/api/users/auth")
public class AuthenticationController {

    private final AuthenticationService service;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthenticationController(final AuthenticationService service, UserRepository userRepository, JwtService jwtService) {
        this.service = service;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
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

    @GetMapping("/check")
    public ResponseEntity<?> checkAuth(HttpServletRequest request) {
        try {
            Long userId = jwtService.validateAndExtractUserId(request);

            // Fetch user from database (optional depending on what info you want to return)
            CustomUser user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("loggedIn", false));
            }

            return ResponseEntity.ok(Map.of(
                    "loggedIn", true,
                    "username", user.getEmail(),
                    "userId", user.getId(),
                    "role", user.getRole()
            ));
        } catch (JwtException e) {
            return ResponseEntity.status(401).body(Map.of("loggedIn", false, "error", e.getMessage()));
        }
    }


}

