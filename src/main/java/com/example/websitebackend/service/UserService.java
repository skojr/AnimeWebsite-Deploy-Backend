package com.example.websitebackend.service;

import com.example.websitebackend.auth.DeleteUserRequest;
import com.example.websitebackend.auth.UpdateUserRequest;
import com.example.websitebackend.auth.UserResponse;
import com.example.websitebackend.model.CustomUser;
import com.example.websitebackend.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String getToken(HttpServletRequest request) {
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("jwt")) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        return token;
    }


    public void updateUser(Long id, UpdateUserRequest request) {
        CustomUser user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found."));
        boolean isUpdated = false;

        // Update email only if a new one is provided
        String newEmail = request.getEmail();
        if (newEmail != null && !newEmail.isBlank() && !newEmail.equals(user.getEmail())) {
            if (request.getPassword() == null || request.getPassword().isBlank()) {
                throw new RuntimeException("Password is required to update email.");
            }

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new RuntimeException("Incorrect password.");
            }

            if (userRepository.existsByEmail(newEmail)) {
                throw new RuntimeException("Email address already in use.");
            }

            user.setEmail(newEmail);
            isUpdated = true;
        }

        // Update password only if a new one is provided
        String newPassword = request.getNewPassword();
        if (newPassword != null && !newPassword.isBlank()) {
            if (request.getOldPassword() == null || request.getOldPassword().isBlank()) {
                throw new RuntimeException("Old password is required to update password.");
            }

            if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                throw new RuntimeException("Incorrect old password.");
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            isUpdated = true;
        }

        if (!isUpdated) {
            throw new RuntimeException("No updates were made.");
        }

        userRepository.save(user);
    }


    // Delete the user
    public void deleteUser(Long id, DeleteUserRequest request) {
        CustomUser user = (CustomUser)this.userRepository.findById(id).orElseThrow(() -> {
            return new RuntimeException("User not found.");
        });
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            if (!this.passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new RuntimeException("Incorrect password.");
            } else {
                this.userRepository.delete(user);
            }
        } else {
            throw new RuntimeException("Password is required to delete the account.");
        }
    }

    public Optional<CustomUser> getUser(Long userId) {
        CustomUser user = (CustomUser)this.userRepository.findById(userId).orElseThrow(() -> {
            return new RuntimeException("User not found with id: " + userId);
        });
        return userRepository.findById(userId);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToUserResponse).collect(Collectors.toList());
    }

    private UserResponse mapToUserResponse(CustomUser user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getRole().name());
    }
}