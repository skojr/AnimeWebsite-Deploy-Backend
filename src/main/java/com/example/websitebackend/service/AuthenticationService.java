package com.example.websitebackend.service;

import com.example.websitebackend.auth.AuthenticationRequest;
import com.example.websitebackend.auth.RegisterRequest;
import com.example.websitebackend.model.CustomUser;
import com.example.websitebackend.model.Role;
import com.example.websitebackend.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final CsrfService csrfService;

    public ResponseEntity<?> register(RegisterRequest request, HttpServletResponse response, HttpSession session) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required");
        }

        if (repository.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email is already registered");
        }

        // Save new user
        CustomUser user = CustomUser.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        repository.save(user);

        // Immediately authenticate
        UserDetails userDetails = userDetailsService.createUserDetails(user);
        String jwtToken = jwtService.generateToken(userDetails);
        String csrfToken = csrfService.generateToken(session);

        // Set cookies
        Cookie csrfCookie = new Cookie("csrfToken", csrfToken);
        csrfCookie.setHttpOnly(false);
        csrfCookie.setSecure(true);
        csrfCookie.setPath("/");
        csrfCookie.setMaxAge(3600);
        response.addCookie(csrfCookie);

        Cookie jwtCookie = new Cookie("jwt", jwtToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(3600);
        response.addCookie(jwtCookie);

        // Return tokens for Postman use
        Map<String, String> tokens = new HashMap<>();
        tokens.put("message", "User registered and authenticated successfully");
        tokens.put("jwtToken", jwtToken);
        tokens.put("csrfToken", csrfToken);

        return ResponseEntity.ok(tokens);
    }


    public ResponseEntity<?> authenticate(AuthenticationRequest request, HttpServletResponse response, HttpSession session) {
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        CustomUser user = repository.findByEmail(request.getEmail()).orElseThrow();
        UserDetails userDetails = userDetailsService.createUserDetails(user);
        String jwtToken = jwtService.generateToken(userDetails);
        String csrfToken = csrfService.generateToken(session);

        // Cookies
        Cookie csrfCookie = new Cookie("csrfToken", csrfToken);
        csrfCookie.setHttpOnly(false);
        csrfCookie.setSecure(true);
        csrfCookie.setPath("/");
        csrfCookie.setMaxAge(3600);
        response.addCookie(csrfCookie);

        Cookie jwtCookie = new Cookie("jwt", jwtToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(3600);
        response.addCookie(jwtCookie);

        // Return tokens for Postman testing
        Map<String, String> tokens = new HashMap<>();
        tokens.put("message", "Authenticated successfully");
        tokens.put("jwtToken", jwtToken);
        tokens.put("csrfToken", csrfToken);

        return ResponseEntity.ok(tokens);
    }



    public AuthenticationService(final UserRepository repository, final PasswordEncoder passwordEncoder, final JwtService jwtService, final AuthenticationManager authenticationManager, final CustomUserDetailsService userDetailsService, final CsrfService csrfService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.csrfService = csrfService;
    }
}
