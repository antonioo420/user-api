package com.example.user_api.service;

import com.example.user_api.model.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(UUID id);
    void deleteUser(UUID id);
}