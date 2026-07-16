package com.demo.sprintw1.service;

import com.demo.sprintw1.dto.LoginRequest;
import com.demo.sprintw1.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.demo.sprintw1.entity.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class AuthService { //Dependency Injection

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public String login(LoginRequest request) {
        authenticationManager.authenticate( /*Spring Security, bu kullanıcının giriş yapmasını dene.
        Yani login işlemini başlatıyoruz.Email ve şifreyi kontrol eder.Yanlışsa burada exception fırlatır ve metodun
        geri kalanı çalışmaz.*/
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                /*
                Doğrulanmış kullanıcıyı veritabanından alıyoruz.Neden? Çünkü JWT'nin içine koyacağımız: email role gibi
                gibi bilgiler User nesnesinde var.
                 */
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return jwtService.generateToken(user);
        //JWT oluşturuyoruz.Controller'a String olarak dönüyor.Controller da bunu kullanıcıya döndürüyor.


    }

}

/*
new UsernamePasswordAuthenticationToken(...)
Aslında JWT ile alakası yok.Bu sadece Spring Security'ye bilgi taşıyan bir nesne.İçinde şunlar var:

email
password

Yani biz Spring'e küçük bir paket veriyoruz.Diyoruz ki:Al, kullanıcının girdiği bilgiler bunlar.
 */

/*
AuthService'de bu satırı yazdık:

authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        )
);

Ama bu metodu çağıran kimse yok. Yani şu an Postman'den: POST /login atarsak

Böyle bir endpoint yok.

çünkü henüz AuthController yazmadık.
 */