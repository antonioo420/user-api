package com.example.user_api.controller;

import com.example.user_api.model.User;
import com.example.user_api.dto.UserResponse;
import com.example.user_api.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> getAll() {
        return userService.getAllUsers().stream()
                .map(user -> new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getCreationDate(), ""))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getCreationDate(), "");
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        userService.deleteUser(id);
    }
}
