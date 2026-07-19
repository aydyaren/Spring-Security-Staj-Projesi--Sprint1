package com.demo.sprintw1.config;

import com.demo.sprintw1.entity.Role;
import com.demo.sprintw1.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        List<String> roles = List.of( //Rollerin isimlerini bir listeye koyuyoruz.
                "ADMIN",
                "MANAGER",
                "EMPLOYEE"
        );

        for (String roleName : roles) { //Listedeki her rol için tek tek çalışıyor.

            if (roleRepository.findByName(roleName).isEmpty()) { //O anki rolü veritabanında arıyor.

                Role role = new Role();
                role.setName(roleName); //O anki ismi (ADMIN, MANAGER veya EMPLOYEE) role nesnesine veriyor.

                roleRepository.save(role); //Veritabanına kaydediyor.


            }
        }

    }
}
