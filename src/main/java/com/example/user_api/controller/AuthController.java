package com.example.user_api.controller;

import com.example.user_api.dto.UserResponse;
import com.example.user_api.dto.LoginRequest;
import com.example.user_api.dto.RegisterRequest;
import com.example.user_api.service.AuthService;
import com.example.user_api.model.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @PostMapping("/register")
    public UserResponse register(@RequestBody RegisterRequest registerRequest) {
        return authService.register(registerRequest);
    }
}
