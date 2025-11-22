package com.example.user_api.dto;

import java.util.UUID;
import java.time.LocalDateTime;

public class UserResponse {
    private UUID id;
    private String username;
    private String email;
    private LocalDateTime creationDate;
    private String token;

    public UserResponse() {}

    public UserResponse(UUID id, String username, String email, LocalDateTime creationDate, String token) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.creationDate = creationDate;
        this.token = token;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}