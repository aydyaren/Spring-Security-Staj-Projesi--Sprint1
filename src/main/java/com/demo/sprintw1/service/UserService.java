package com.demo.sprintw1.service;

import com.demo.sprintw1.dto.request.CreateUserRequest;
import com.demo.sprintw1.dto.request.UpdateUserRequest;
import com.demo.sprintw1.dto.response.UserResponse;
import com.demo.sprintw1.entity.Role;
import com.demo.sprintw1.entity.User;
import com.demo.sprintw1.repository.RoleRepository;
import com.demo.sprintw1.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor Dependency Injection:
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(CreateUserRequest request) {

        // DTO'dan roleId alıyoruz.
        // RoleRepository ile veritabanında bu ID'yi arıyoruz.
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Role not found"
                ));
        /*
         Bulamazsak
         .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found"));
         çalışır ve kullanıcı oluşturulmaz.
         */

        User user = new User();

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        // Kullanıcının girdiği şifre BCrypt ile hashlenerek veritabanına kaydedilir.
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(role);

        User savedUser = userRepository.save(user);

        // Entity yerine DTO döndürüyoruz (şifre hash'i client'a asla sızmasın diye).
        return mapToResponse(savedUser);
    }

    /*
     Entity'yi doğrudan kullanıcıya göndermek yerine
     UserResponse DTO'suna dönüştürüyoruz.
     Böylece istemciye sadece göstermek istediğimiz
     alanlar gönderiliyor.
     */
    private UserResponse mapToResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().getName()
        );
    }

    /*
     Veritabanındaki bütün kullanıcıları alır.
     Her User nesnesini UserResponse DTO'suna çevirir
     ve liste halinde Controller'a döndürür.
     */
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public UserResponse updateUser(Long id, UpdateUserRequest request) {
// Güncellenecek kullanıcıyı veritabanında arıyoruz.
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        // DTO'dan gelen roleId ile yeni rolü buluyoruz.
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Role not found"
                ));
        // Kullanıcının bilgilerini güncelliyoruz.
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(role);

        // Güncellenen kullanıcıyı kaydediyoruz.
        User updatedUser = userRepository.save(user);

        // Entity yerine DTO döndürüyoruz.
        return mapToResponse(updatedUser);
    }

    /*
    Kullanıcıyı ID'ye göre siler.
     Kullanıcı bulunamazsa hata fırlatır.
    */
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        userRepository.delete(user);
    }

}