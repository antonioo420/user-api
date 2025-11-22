package com.example.user_api.service.impl;

import com.example.user_api.dto.LoginRequest;
import com.example.user_api.dto.RegisterRequest;
import com.example.user_api.dto.UserResponse;
import com.example.user_api.model.User;
import com.example.user_api.repository.UserRepository;
import com.example.user_api.service.AuthService;
import com.example.user_api.exception.UnauthorizedException;
import com.example.user_api.exception.BadRequestException;
import com.example.user_api.exception.ConflictException;
import com.example.user_api.security.JwtService;

import java.time.LocalDateTime;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder passwordEncoder,
                           JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override 
    public UserResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new UnauthorizedException("Username not found"));
        
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Wrong password");
        }

        String token = jwtService.generateToken(user.getUsername());
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getCreationDate(),
            token);
    }

    @Override
    public UserResponse register(RegisterRequest request) {

        if (request.getPassword().length() < 8) {
            throw new BadRequestException("Password must contain at least 8 characters");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreationDate(LocalDateTime.now());

        userRepository.save(user);
        
        String token = jwtService.generateToken(user.getUsername());
        
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getCreationDate(),
            token);
    }

}