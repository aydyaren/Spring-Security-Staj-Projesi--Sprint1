package com.demo.sprintw1.controller;

import com.demo.sprintw1.dto.request.CreateUserRequest;
import com.demo.sprintw1.dto.request.UpdateUserRequest;
import com.demo.sprintw1.dto.response.UserResponse;
import com.demo.sprintw1.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import jakarta.validation.Valid;
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
    public UserResponse createUser(@Valid @RequestBody CreateUserRequest request) {
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

    /*
    ID'ye göre tek bir kullanıcıyı döndürür.
    Bu endpoint'e sadece ADMIN erişebilir.
    */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {

        return userService.getUserById(id);
    }

    /*
    Giriş yapan kullanıcının kendi profil bilgilerini döndürür.
    ADMIN, MANAGER ve EMPLOYEE erişebilir.
    */
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    @GetMapping("/me")
    public UserResponse getMyProfile(Authentication authentication) {

        return userService.getMyProfile(authentication.getName());
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
    public ResponseEntity<Void> deleteUser(

            @PathVariable Long id,

            @RequestParam(defaultValue = "false")
            boolean force

    ) {

        userService.deleteUser(id, force);

        return ResponseEntity.noContent().build();

    }

}