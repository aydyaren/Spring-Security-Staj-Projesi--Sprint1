package com.demo.sprintw1.config;

import com.demo.sprintw1.entity.Role;
import com.demo.sprintw1.entity.User;
import com.demo.sprintw1.repository.RoleRepository;
import com.demo.sprintw1.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.demo.sprintw1.exception.RoleNotFoundException;

@Component
public class AdminInitializer implements CommandLineRunner {

    private static final Logger log =
            LoggerFactory.getLogger(AdminInitializer.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.default-admin.email}")
    private String adminEmail;

    @Value("${app.default-admin.password}")
    private String adminPassword;

    public AdminInitializer(UserRepository userRepository,
                            RoleRepository roleRepository,
                            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // Admin kullanıcı daha önce oluşturulmuşsa tekrar oluşturma.
        if (userRepository.findByEmail(adminEmail).isPresent()) {
            return;
        }

        Role adminRole = roleRepository.findByName("ADMIN").orElse(null);

        if (adminRole == null) {
            log.error("ADMIN role not found. Default admin user was not created.");
            return;
        }
        User admin = new User();

        admin.setFirstName("System");
        admin.setLastName("Administrator");
        admin.setUsername("admin");
        admin.setEmail(adminEmail);

        // BCrypt ile şifreyi hashle.
        admin.setPassword(passwordEncoder.encode(adminPassword));

        admin.setRole(adminRole);

        userRepository.save(admin);

        log.info("Default admin user created.");
    }
}