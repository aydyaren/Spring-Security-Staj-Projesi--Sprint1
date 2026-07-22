package com.demo.sprintw1.service;

import com.demo.sprintw1.dto.request.LoginRequest;
import com.demo.sprintw1.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.demo.sprintw1.dto.response.AuthResponse;
import com.demo.sprintw1.dto.response.AuthenticationResult;
import com.demo.sprintw1.service.RefreshTokenService;

import com.demo.sprintw1.entity.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import com.demo.sprintw1.entity.RefreshToken;



@Service
public class AuthService { //Dependency Injection

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       JwtService jwtService,
                       RefreshTokenService refreshTokenService) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthenticationResult login(LoginRequest request)  {

        /*
         Kullanıcı login alanına ister e-mail ister username yazabilir.
         Önce veritabanında her iki alanda da arıyoruz.
         */
        User user = userRepository
                .findByEmailOrUsername(request.getLogin(), request.getLogin())
                /*
                Doğrulanmış kullanıcıyı veritabanından alıyoruz.
                Neden? Çünkü JWT'nin içine koyacağımız:
                email, role gibi bilgiler User nesnesinde var.
                 */
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        authenticationManager.authenticate(
                /*Spring Security, bu kullanıcının giriş yapmasını dene.
                Yani login işlemini başlatıyoruz.
                Email ve şifreyi kontrol eder.
                Yanlışsa burada exception fırlatır ve metodun
                geri kalanı çalışmaz.*/
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),      // Spring Security mevcut yapıda email ile çalışıyor.
                        request.getPassword()
                )
        );

        // Access Token oluşturulur.
        String accessToken = jwtService.generateToken(user);

        // Refresh Token oluşturulur.
        String refreshToken = refreshTokenService.createRefreshToken(user);

        // Access Token ve Refresh Token Controller'a gönderilir.
        return new AuthenticationResult(accessToken, refreshToken);
    }

    /*
     * Geçerli bir Refresh Token kullanarak yeni Access Token ve Refresh Token üretir.
     */
    public AuthenticationResult refreshToken(String refreshTokenValue) {

        RefreshToken refreshToken =
                refreshTokenService.findByToken(refreshTokenValue);

        // Refresh Token kullanılabilir mi kontrol et.
        refreshTokenService.validateRefreshToken(refreshToken);

        // Refresh Token'ın sahibi olan kullanıcıyı rol bilgisiyle birlikte yükle.
        User user = userRepository
                .findByIdWithRole(refreshToken.getUser().getId())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        // Eski Refresh Token'ı iptal et.
        refreshTokenService.revokeToken(refreshToken);

        // Yeni Access Token oluştur.
        String accessToken = jwtService.generateToken(user);

        // Yeni Refresh Token oluştur.
        String newRefreshToken =
                refreshTokenService.createRefreshToken(user);

        // Yeni tokenlar Controller'a gönderilir.
        return new AuthenticationResult(accessToken, newRefreshToken);
    }

    public void logout(String refreshTokenValue) {

        RefreshToken refreshToken =
                refreshTokenService.findByToken(refreshTokenValue);

        refreshTokenService.validateRefreshToken(refreshToken);

        refreshTokenService.revokeToken(refreshToken);
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
                user.getEmail(),
                request.getPassword()
        )
);

Ama bu metodu çağıran kimse yok. Yani şu an Postman'den: POST /login atarsak

Böyle bir endpoint yok.

çünkü henüz AuthController yazmadık.
*/