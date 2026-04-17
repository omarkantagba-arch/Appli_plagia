package com.plagicheck.service;

import com.plagicheck.dto.AuthResponse;
import com.plagicheck.dto.LoginRequest;
import com.plagicheck.dto.RegisterRequest;
import com.plagicheck.entity.User;
import com.plagicheck.repository.UserRepository;
import com.plagicheck.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setNom(request.getNom());
        user.setPrenom(request.getPrenom());
        user.setRole(User.Role.valueOf(request.getRole().toUpperCase()));
        user.setFiliere(request.getFiliere());

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(token, user.getEmail(), user.getNom(), user.getPrenom(), 
                user.getRole().name(), user.getFiliere());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(token, user.getEmail(), user.getNom(), user.getPrenom(), 
                user.getRole().name(), user.getFiliere());
    }
}
