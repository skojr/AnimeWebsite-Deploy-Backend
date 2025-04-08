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
    private final CsrfService csrfService;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;

    public UserController(UserService userService, JwtService jwtService, CsrfService csrfService, UserRepository userRepository, @Qualifier("userDetailsService") UserDetailsService userDetailsService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.csrfService = csrfService;
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    @PutMapping({"users/updateUser"})
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> updateUser(@RequestHeader("X-CSRF-TOKEN") String csrfToken,
                                        @RequestBody UpdateUserRequest updateUserRequest,
                                        HttpServletRequest httpServletRequest,
                                        HttpSession session) {
        try {
            // Validate CSRF token
            csrfService.validateCsrfToken(session, csrfToken);

            // Validate JWT token and extract user ID
            Long userId = jwtService.validateAndExtractUserId(httpServletRequest);

            // Ensure user exists
            Optional<CustomUser> userOpt = userService.getUser(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            // Perform the update
            userService.updateUser(userId, updateUserRequest);
            return ResponseEntity.noContent().build(); // 204 No Content for successful updates

        } catch (CsrfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping({"users/deleteUser"})
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> deleteUser(@RequestHeader("X-CSRF-TOKEN") String csrfToken,
                                        @RequestBody DeleteUserRequest deleteRequest,
                                        HttpServletRequest httpServletRequest,
                                        HttpSession session) {
        try {
            // Validate CSRF token
            csrfService.validateCsrfToken(session, csrfToken);

            // Validate JWT token and extract user ID
            Long userId = jwtService.validateAndExtractUserId(httpServletRequest);

            // Ensure the authenticated user is authorized to delete this resource
            Optional<CustomUser> userOpt = userService.getUser(userId);

            // Check if user exists
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            // Perform the deletion
            userService.deleteUser(userId, deleteRequest);
            return ResponseEntity.noContent().build(); // 204 No Content for successful deletions

        } catch (CsrfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/users/getUser")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("X-CSRF-TOKEN") String csrfToken,
                                            HttpServletRequest httpServletRequest,
                                            HttpSession session) {
        try {
            // Validate CSRF token
            csrfService.validateCsrfToken(session, csrfToken);

            // Validate JWT token and extract user ID
            Long userId = jwtService.validateAndExtractUserId(httpServletRequest);

            // Fetch user from the database
            Optional<CustomUser> userOpt = userService.getUser(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            // Map user to a response object
            CustomUser user = userOpt.get();
            System.out.println(user.getId());
            UserResponse userResponse = UserResponse.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .build();

            return ResponseEntity.ok(userResponse);

        } catch (CsrfException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }




//    @GetMapping({"users/{id}"})
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
//    public ResponseEntity<?> getUserById(@PathVariable Long id,
//                                         @RequestHeader("X-CSRF-TOKEN") String csrfToken,
//                                         HttpServletRequest httpServletRequest,
//                                         HttpSession session) {
//        try {
//            // Validate CSRF token
//            csrfService.validateCsrfToken(session, csrfToken);
//
//            // Validate JWT token and extract user ID
//            Long tokenUserId = jwtService.validateAndExtractUserId(httpServletRequest);
//
//            // Ensure the authenticated user is authorized to get this resource
//            if (!tokenUserId.equals(id)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User is not authorized to get this resource");
//            }
//
//            // Get and return the user
//            Optional<CustomUser> user = userService.getUser(id);
//            UserResponse userResponse = UserResponse.builder()
//                    .id(user.get().getId())
//                    .email(user.get().getUsername())
//                    .role(user.get().getRole().toString())
//                    .build();
//            return ResponseEntity.ok(userResponse);
//
//        } catch (CsrfException e) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
//        } catch (JwtException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }


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
