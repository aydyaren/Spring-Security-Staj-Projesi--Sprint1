package com.demo.sprintw1.controller;

import com.demo.sprintw1.dto.CreateUserRequest;
import com.demo.sprintw1.entity.User;
import com.demo.sprintw1.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
