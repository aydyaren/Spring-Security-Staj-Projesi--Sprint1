package com.demo.sprintw1.controller;

import com.demo.sprintw1.dto.request.LoginRequest;
import com.demo.sprintw1.dto.request.LogoutRequest;
import com.demo.sprintw1.dto.response.AuthResponse;
import com.demo.sprintw1.exception.InvalidRefreshTokenException;
import com.demo.sprintw1.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.demo.sprintw1.dto.response.AuthenticationResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.CookieValue;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /*
     * Kullanıcı giriş işlemini gerçekleştirir.Başarılı olursa Access Token ve Refresh Token döndürür.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {

        AuthenticationResult result = authService.login(request);

        ResponseCookie refreshCookie = ResponseCookie.from(
                        "refreshToken",
                        result.getRefreshToken()
                )
                .httpOnly(true)
                .secure(false) // Local geliştirme ortamı için false.
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        AuthResponse response = new AuthResponse(result.getAccessToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(response);
    }
    /*
     Geçerli bir Refresh Token kullanarak yeni Access Token ve Refresh Token üretir.
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(
            @CookieValue(value = "refreshToken", required = false)
            String refreshToken) {

        // Cookie gönderilmemişse kullanıcı login değildir.
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidRefreshTokenException("Refresh Token is missing.");
        }

        AuthenticationResult result =
                authService.refreshToken(refreshToken);

        ResponseCookie refreshCookie = ResponseCookie.from(
                        "refreshToken",
                        result.getRefreshToken()
                )
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();

        AuthResponse response =
                new AuthResponse(result.getAccessToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue("refreshToken") String refreshToken) {

        authService.logout(refreshToken);

        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }
}