package com.example.websitebackend.service;

import com.example.websitebackend.auth.AuthenticationRequest;
import com.example.websitebackend.auth.AuthenticationResponse;
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

    public AuthenticationResponse register(RegisterRequest request) {
        CustomUser user = CustomUser.builder().email(request.getEmail()).password(this.passwordEncoder.encode(request.getPassword())).role(Role.USER).build();
        this.repository.save(user);
        UserDetails userDetails = this.userDetailsService.createUserDetails(user);
        String jwtToken = this.jwtService.generateToken(userDetails);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        CustomUser user = (CustomUser)this.repository.findByEmail(request.getEmail()).orElseThrow();
        UserDetails userDetails = this.userDetailsService.createUserDetails(user);
        String jwtToken = this.jwtService.generateToken(userDetails);
        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationService(final UserRepository repository, final PasswordEncoder passwordEncoder, final JwtService jwtService, final AuthenticationManager authenticationManager, final CustomUserDetailsService userDetailsService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }
}