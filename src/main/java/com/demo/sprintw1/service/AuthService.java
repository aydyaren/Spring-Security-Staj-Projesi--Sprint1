package com.demo.sprintw1.service;

import com.demo.sprintw1.audit.AuditAction;
import com.demo.sprintw1.audit.AuditResource;
import com.demo.sprintw1.dto.request.LoginRequest;
import com.demo.sprintw1.dto.response.AuthenticationResult;
import com.demo.sprintw1.entity.RefreshToken;
import com.demo.sprintw1.entity.User;
import com.demo.sprintw1.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService { //Dependency Injection

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final RefreshTokenService refreshTokenService;

    private final AuditLogService auditLogService;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       JwtService jwtService,
                       RefreshTokenService refreshTokenService,
                       AuditLogService auditLogService) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.auditLogService = auditLogService;
    }

    public AuthenticationResult login(LoginRequest request) {

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

        // Login işlemini audit tablosuna kaydet.
        auditLogService.saveLog(
                user.getEmail(),
                AuditAction.LOGIN,
                AuditResource.AUTH,
                null
        );

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

        // Refresh Token işlemini audit tablosuna kaydet.
        auditLogService.saveLog(
                user.getEmail(),
                AuditAction.REFRESH_TOKEN,
                AuditResource.AUTH,
                null
        );

        // Yeni tokenlar Controller'a gönderilir.
        return new AuthenticationResult(accessToken, newRefreshToken);
    }

    public void logout(String refreshTokenValue) {

        RefreshToken refreshToken =
                refreshTokenService.findByToken(refreshTokenValue);

        refreshTokenService.validateRefreshToken(refreshToken);

        refreshTokenService.revokeToken(refreshToken);

        // Logout işlemini audit tablosuna kaydet.
        auditLogService.saveLog(
                refreshToken.getUser().getEmail(),
                AuditAction.LOGOUT,
                AuditResource.AUTH,
                null
        );
    }

}