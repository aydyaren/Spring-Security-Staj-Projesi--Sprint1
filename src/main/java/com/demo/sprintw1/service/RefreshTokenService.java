package com.demo.sprintw1.service;

import com.demo.sprintw1.entity.RefreshToken;
import com.demo.sprintw1.entity.User;
import com.demo.sprintw1.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.UUID;

import com.demo.sprintw1.exception.InvalidRefreshTokenException;
import com.demo.sprintw1.exception.RefreshTokenExpiredException;
import com.demo.sprintw1.exception.RefreshTokenRevokedException;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    // Refresh Token'ın kaç gün geçerli olacağı. Süresini buraya yazmıyoruz, application.properties içinden okunacak.
    @Value("${jwt.refresh-expiration-days}")
    private long refreshExpirationDays;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /*
     * Kullanıcı için yeni bir Refresh Token oluşturur.
     * Veritabanına yalnızca hash'i kaydedilir, gerçek token kullanıcıya döndürülür.
     */
    public String createRefreshToken(User user) {

        RefreshToken refreshToken = new RefreshToken();

        // Rastgele benzersiz Refresh Token oluşturur.
        String token = UUID.randomUUID().toString();

        // Gerçek token yerine hash'i veritabanına kaydedilir.
        refreshToken.setTokenHash(hashToken(token));
        refreshToken.setCreatedAt(LocalDateTime.now());
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(refreshExpirationDays));
        refreshToken.setRevoked(false);
        refreshToken.setUser(user);

        refreshTokenRepository.save(refreshToken);

        // Gerçek token frontend'e gönderilir.
        return token;
    }

    /*
     Gönderilen Refresh Token'ın hash'ini oluşturur ve veritabanında kayıtlı olup olmadığını kontrol eder.
     */
    public RefreshToken findByToken(String token) {

        return refreshTokenRepository.findByTokenHash(hashToken(token))
                .orElseThrow(() ->
                        new InvalidRefreshTokenException("Refresh Token not found."));
    }

    /*
     * Refresh Token'ın kullanılabilir olup olmadığını kontrol eder.
     */
    public void validateRefreshToken(RefreshToken refreshToken) {

        if (refreshToken.isRevoked()) {
            throw new RefreshTokenRevokedException(
                    "Refresh Token has been revoked."
            );
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RefreshTokenExpiredException(
                    "Refresh Token has expired."
            );
        }

    }

    // Logout sırasında Refresh Token iptal edilir.
    public void revokeToken(RefreshToken refreshToken) {

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }

    /*
     * Refresh Token'ın SHA-256 hash'ini oluşturur.
     * Veritabanında gerçek token yerine yalnızca hash'i saklanacaktır.
     */
    private String hashToken(String token) {

        try {

            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = messageDigest.digest(token.getBytes(StandardCharsets.UTF_8));

            StringBuilder hash = new StringBuilder();

            for (byte b : hashBytes) {
                hash.append(String.format("%02x", b));
            }

            return hash.toString();

        } catch (NoSuchAlgorithmException e) {

            throw new RuntimeException("Refresh Token hash oluşturulamadı.", e);

        }
    }
}