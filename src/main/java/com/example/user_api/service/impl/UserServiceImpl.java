package com.example.user_api.security.impl;

import com.example.user_api.model.User;
import com.example.user_api.repository.UserRepository;
import com.example.user_api.service.UserService;
import com.example.user_api.exception.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;


    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository; 
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Username not found"));
    }

    @Override
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)){
            throw new NotFoundException("Cannot delete. User not found with id: "+ id);
        }
        userRepository.deleteById(id);
    }
}
