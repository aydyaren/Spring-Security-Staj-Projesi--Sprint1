package com.demo.sprintw1.service;


import com.demo.sprintw1.dto.CreateUserRequest;
import com.demo.sprintw1.entity.Role;
import com.demo.sprintw1.entity.User;
import com.demo.sprintw1.repository.RoleRepository;
import com.demo.sprintw1.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(CreateUserRequest request) {

        //DTO'dan roleId alıyoruz.RoleRepository ile veritabanında bu ID'yi arıyoruz.
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        /* Bulamazsak ".orElseThrow(() -> new RuntimeException("Role not found"));"
        çalışıyor ve kullanıcı oluşturulmuyor.*/
        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        return userRepository.save(user);
    }

}
