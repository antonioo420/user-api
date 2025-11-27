package com.example.user_api.service;

import com.example.user_api.dto.LoginRequest;
import com.example.user_api.dto.RegisterRequest;
import com.example.user_api.dto.UserResponse;

public interface AuthService {
    UserResponse login(LoginRequest loginRequest);
    UserResponse register(RegisterRequest registerRequest);
}