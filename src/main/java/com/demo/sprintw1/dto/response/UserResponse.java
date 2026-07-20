package com.demo.sprintw1.dto.response;

public class UserResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String role;

    public UserResponse(Long id,
                        String firstName,
                        String lastName,
                        String username,
                        String email,
                        String role) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}