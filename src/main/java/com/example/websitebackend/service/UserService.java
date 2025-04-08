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

    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        CustomUser user = (CustomUser)this.userRepository.findById(id).orElseThrow(() -> {
            return new RuntimeException("User not found.");
        });
        boolean isUpdated = false;
        if (request.getEmail() != null && !request.getEmail().isEmpty()) {
            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                throw new RuntimeException("Password is required to update email.");
            }

            if (!this.passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new RuntimeException("Incorrect password.");
            }

            if (this.userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email address already in use.");
            }

            user.setEmail(request.getEmail());
            isUpdated = true;
        }

        if (request.getNewPassword() != null && !request.getNewPassword().isEmpty()) {
            if (request.getOldPassword() == null || request.getOldPassword().isEmpty()) {
                throw new RuntimeException("Old password is required to update password.");
            }

            if (!this.passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
                throw new RuntimeException("Incorrect old password.");
            }

            user.setPassword(this.passwordEncoder.encode(request.getNewPassword()));
            isUpdated = true;
        }

        if (!isUpdated) {
            throw new RuntimeException("No updates were made.");
        } else {
            CustomUser updatedUser = (CustomUser)this.userRepository.save(user);
            return this.mapToUserResponse(updatedUser);
        }
    }

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

    public UserResponse getUser(Long userId) {
        CustomUser user = (CustomUser)this.userRepository.findById(userId).orElseThrow(() -> {
            return new RuntimeException("User not found with id: " + userId);
        });
        return this.mapToUserResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        return (List)this.userRepository.findAll().stream().map(this::mapToUserResponse).collect(Collectors.toList());
    }

    private UserResponse mapToUserResponse(CustomUser user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getRole().name());
    }

    public UserService(final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
}