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
public class AuthService {

    // Spring Security AuthenticationManager
    private final AuthenticationManager authenticationManager;

    // Kullanıcı işlemleri
    private final UserRepository userRepository;

    // JWT işlemleri
    private final JwtService jwtService;

    // Refresh Token işlemleri
    private final RefreshTokenService refreshTokenService;

    // Audit Log işlemleri
    private final AuditLogService auditLogService;

    // Login Rate Limit işlemleri
    private final LoginRateLimitService loginRateLimitService;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       JwtService jwtService,
                       RefreshTokenService refreshTokenService,
                       LoginRateLimitService loginRateLimitService,
                       AuditLogService auditLogService) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.loginRateLimitService = loginRateLimitService;
        this.auditLogService = auditLogService;
    }

    public AuthenticationResult login(LoginRequest request) {

        // Login (username veya e-mail) için rate limit kontrolü yapıyoruz.
        loginRateLimitService.checkLoginLimit(request.getLogin());

        /*
         Kullanıcı login alanına ister e-mail ister username yazabilir.
         Önce veritabanında her iki alanda da arıyoruz.
         */
        User user = userRepository
                .findByEmailOrUsername(request.getLogin(), request.getLogin())
                /*
                 Doğrulanmış kullanıcıyı veritabanından alıyoruz.
                 Çünkü JWT'nin içine koyacağımız email, role gibi bilgiler
                 User nesnesinde bulunuyor.
                 */
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Spring Security ile kullanıcı doğrulaması yapıyoruz.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
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

        // Yeni tokenları Controller'a gönder.
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