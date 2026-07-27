package com.demo.sprintw1.service;

import com.demo.sprintw1.dto.request.CreateUserRequest;
import com.demo.sprintw1.dto.request.UpdateUserRequest;
import com.demo.sprintw1.dto.response.UserResponse;
import com.demo.sprintw1.entity.RefreshToken;
import com.demo.sprintw1.entity.Role;
import com.demo.sprintw1.entity.User;
import com.demo.sprintw1.repository.RefreshTokenRepository;
import com.demo.sprintw1.repository.RoleRepository;
import com.demo.sprintw1.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenRepository refreshTokenRepository;

    // Constructor Dependency Injection:
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       RefreshTokenRepository refreshTokenRepository) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public UserResponse createUser(CreateUserRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already exists."
            );
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Username already exists."
            );
        }
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
    /*
    ID'ye göre tek bir kullanıcıyı getirir.
     Kullanıcı bulunamazsa 404 döndürür.
    */
    public UserResponse getUserById(Long id) {

        User user = userRepository.findByIdWithRole(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        return mapToResponse(user);
    }


    public UserResponse updateUser(Long id, UpdateUserRequest request) {

        // Güncellenecek kullanıcıyı veritabanında arıyoruz.
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        // Email başka bir kullanıcı tarafından kullanılıyor mu?
        userRepository.findByEmail(request.getEmail())
                .ifPresent(existingUser -> {
                    if (!existingUser.getId().equals(id)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Email already exists."
                        );
                    }
                });

        // Username başka bir kullanıcı tarafından kullanılıyor mu?
        userRepository.findByUsername(request.getUsername())
                .ifPresent(existingUser -> {
                    if (!existingUser.getId().equals(id)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Username already exists."
                        );
                    }
                });

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
   /*
 Kullanıcıyı ID'ye göre siler.
 Önce kullanıcıya ait Refresh Token kayıtlarını siler.
 Daha sonra kullanıcıyı veritabanından kaldırır.
*/
    @Transactional
    public void deleteUser(Long id) {

        System.out.println("******** DELETE USER CALLED ********");

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        List<RefreshToken> tokens = refreshTokenRepository.findByUser(user);

        System.out.println("Token sayısı = " + tokens.size());

        refreshTokenRepository.deleteAll(tokens);

        System.out.println("Tokenlar silindi.");

        userRepository.delete(user);
    }
    /*
    Giriş yapan kullanıcının kendi profil bilgilerini döndürür.
    JWT içerisindeki email bilgisi kullanılarak kullanıcı bulunur.
    */
    public UserResponse getMyProfile(String email) {

        User user = userRepository.findByEmailWithRole(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        return mapToResponse(user);
    }
}