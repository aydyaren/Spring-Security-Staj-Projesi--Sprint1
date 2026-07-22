package com.demo.sprintw1.repository;

import com.demo.sprintw1.entity.RefreshToken;
import com.demo.sprintw1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    // Verilen token ile RefreshToken kaydını bulur.
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    // Bir kullanıcıya ait bütün Refresh Token'ları getirir.
    List<RefreshToken> findByUser(User user);

    // Sadece iptal edilmemiş Refresh Token'ı getirir.
    Optional<RefreshToken> findByTokenHashAndRevokedFalse(String tokenHash);



}