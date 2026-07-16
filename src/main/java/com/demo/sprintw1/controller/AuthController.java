package com.demo.sprintw1.controller;

import com.demo.sprintw1.dto.LoginRequest;
import com.demo.sprintw1.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        return authService.login(request);

    }
}