package com.demo.sprintw1.controller;

import com.demo.sprintw1.dto.CreateUserRequest;
import com.demo.sprintw1.entity.User;
import com.demo.sprintw1.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    //Controller'ın UserService ile konuşmasını sağlar.
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public User createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    @GetMapping
    public String test(Authentication authentication) {
        return "Hoş geldin " + authentication.getName();
    }
}

