package com.example.websitebackend.controller;

import com.example.websitebackend.auth.DeleteUserRequest;
import com.example.websitebackend.auth.UpdateUserRequest;
import com.example.websitebackend.auth.UserResponse;
import com.example.websitebackend.model.CustomUser;
import com.example.websitebackend.model.Role;
import com.example.websitebackend.repository.UserRepository;
import com.example.websitebackend.service.CsrfService;
import com.example.websitebackend.service.JwtService;
import com.example.websitebackend.service.UserService;
import java.util.List;
import java.util.Optional;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.csrf.CsrfException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api"})
public class UserController {
    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PutMapping({"users/{id}"})
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long tokenUserId = this.jwtService.extractUserId(token);
        if (!tokenUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Authorization Denied");
        } else {
            try {
                UserResponse updatedUser = this.userService.updateUser(id, request);
                return ResponseEntity.ok(updatedUser);
            } catch (RuntimeException var7) {
                RuntimeException e = var7;
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }
    }

    @DeleteMapping({"users/{id}"})
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id, @RequestHeader("Authorization") String authHeader, @RequestBody DeleteUserRequest request) {
        try {
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                if (token.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Empty token");
                } else {
                    Long tokenUserId;
                    try {
                        tokenUserId = this.jwtService.extractUserId(token);
                    } catch (Exception var7) {
                        Exception e = var7;
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token: " + e.getMessage());
                    }

                    if (!tokenUserId.equals(id)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Authorization Denied");
                    } else {
                        this.userService.deleteUser(id, request);
                        return ResponseEntity.noContent().build();
                    }
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Authorization header");
            }
        } catch (RuntimeException var8) {
            RuntimeException e = var8;
            System.out.println("Error deleting user");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception var9) {
            System.out.println("Unexpected error deleting user");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping({"users/{id}"})
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getUserById(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long tokenUserId = this.jwtService.extractUserId(token);
        if (!tokenUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Authorization Denied");
        } else {
            try {
                UserResponse user = this.userService.getUser(id);
                return ResponseEntity.ok(user);
            } catch (RuntimeException var6) {
                RuntimeException e = var6;
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
        }
    }

    @GetMapping({"/admin/getAll"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserResponse> users = this.userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (RuntimeException var2) {
            RuntimeException e = var2;
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    private boolean isNotAuthorized(CustomUser currentUser, Long userId) {
        return !currentUser.getId().equals(userId) && currentUser.getRole() != Role.ADMIN;
    }
}