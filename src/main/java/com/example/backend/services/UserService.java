package com.example.backend.services;

import com.example.backend.dto.DeleteRequestDTO;
import com.example.backend.dto.UpdateRequestDTO;
import com.example.backend.dto.UserResponseDTO;
import com.example.backend.model.User;
import com.example.backend.repositories.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<UserResponseDTO> getUser(Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("No authentication found");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(new UserResponseDTO(user));
    }

    public void deleteUser(Authentication authentication, DeleteRequestDTO deleteRequestDTO) {
        if (authentication == null) {
            throw new RuntimeException("No authentication found");
        }
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(deleteRequestDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        userRepository.delete(user);
    }

    public ResponseEntity<UserResponseDTO> updateUser(Authentication authentication, UpdateRequestDTO updateRequestDTO) {
        if (authentication == null) {
            throw new RuntimeException("No authentication provided");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(updateRequestDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        if (updateRequestDTO.getNewUsername() != null && !updateRequestDTO.getNewUsername().trim().isEmpty()) {
            user.setUsername(updateRequestDTO.getNewUsername().trim());
        }
        if (updateRequestDTO.getNewPassword() != null && !updateRequestDTO.getNewPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateRequestDTO.getNewPassword().trim()));
        }

        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(new UserResponseDTO(savedUser));
    }
}
