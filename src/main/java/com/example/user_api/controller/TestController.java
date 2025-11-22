package com.example.user_api.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/all")
    public String publicEndpoint() {
        return "Anyone can access this endpoint";
    }

    @GetMapping("/user")
    public String privateEndpoint() {
        return "Only users authenticated can access this endpoint";
    }
}
