package com.example.backend.controllers;

import com.example.backend.dto.DeleteRequestDTO;
import com.example.backend.dto.UpdateRequestDTO;
import com.example.backend.dto.UserResponseDTO;
import com.example.backend.model.User;
import com.example.backend.repositories.UserRepository;
import com.example.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.getUser(authentication);
    }

    @DeleteMapping("/me")
    public void deleteUser(@RequestBody DeleteRequestDTO deleteRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        userService.deleteUser(authentication, deleteRequestDTO);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateUser(@RequestBody UpdateRequestDTO updateRequestDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return userService.updateUser(authentication, updateRequestDTO);
    }

}
