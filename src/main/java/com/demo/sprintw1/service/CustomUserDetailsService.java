package com.demo.sprintw1.service;

import com.demo.sprintw1.entity.User; // Bu bizim entity'miz.
import com.demo.sprintw1.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService (UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        System.out.println(">>> findByEmailWithRole CALLED <<<");

        User user = userRepository.findByEmailWithRole(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().getName())
                .build();
    }
}
/*throws UsernameNotFoundException
Kullanıcı giriş yapıyor.

{
   "email":"abc@gmail.com",
   "password":"123456"
}

Ama böyle biri yok.Spring diyor ki:Bu durumda UsernameNotFoundException fırlat.Yani login başarısız olacak.
 */

/*

Bizim User entity'mizde: user.getRole().getName() bize örneğin:

ADMIN döndürüyor. Spring Security bunu "authority" olarak biliyor.O yüzden:

.authorities(user.getRole().getName()) yazacağız.
 */

// Spring Security email'i user name olarak biliyor.

/*
Şu an spring securityye şunu öğrettik : Benim kullanıcılarım veritabanında böyle tutuluyor. Login yaparken
onları böyle bulabilirsin.
Bu metod olmadan Spring Security bizim veritabanımızı tanıyamazdı.
 */

/* Buraya kadar spring security customuserdetailsservice sınıfını kullanamıyor.
Biz yazdık ama haberi yok çünkü SecurityConfig'e daha söylemedik.Yani Spring Security hâlâ kullanıcıları
nereden bulacağımı bilmiyor */



