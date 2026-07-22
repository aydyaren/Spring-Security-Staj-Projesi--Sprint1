package com.demo.sprintw1.controller;

import com.demo.sprintw1.dto.request.CreateUserRequest;
import com.demo.sprintw1.dto.request.UpdateUserRequest;
import com.demo.sprintw1.dto.response.UserResponse;
import com.demo.sprintw1.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    // Controller'ın UserService ile konuşmasını sağlar.
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public UserResponse createUser(@RequestBody CreateUserRequest request) {
        return userService.createUser(request);
    }

    /*
     Sistemdeki bütün kullanıcıları listeler.
     Bu endpoint'e sadece ADMIN rolündeki kullanıcılar erişebilir.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public UserResponse updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {

        return userService.updateUser(id, request);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }
}