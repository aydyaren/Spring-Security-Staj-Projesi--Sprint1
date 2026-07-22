package com.demo.sprintw1.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/*
 * Amaç:Kullanıcının Refresh Token bilgilerini veritabanında tutar.
 *
 * Neden veritabanında tutuyoruz?
 * - Logout yapılabilsin.
 * - Token iptal edilebilsin.
 * - Güvenlik ihlallerinde token devre dışı bırakılabilsin.
 * - Aynı kullanıcının birden fazla cihazdan oturum açabilmesi sağlansın.
 */
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    // Refresh Token kaydının benzersiz kimliği.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Güvenlik nedeniyle gerçek Refresh Token yerine SHA-256 ile hash'lenmiş hali tutulacaktır.
     * İlk aşamada düz token saklayacağız.Daha sonra hash mekanizmasını ekleyeceğiz.
     */
    @Column(nullable = false, unique = true)
    private String tokenHash;

    // Refresh Token'ın oluşturulma zamanı.
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Refresh Token'ın son kullanma tarihi.
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    /*
     * Token iptal edildi mi?
     * false -> kullanılabilir
     * true  -> artık kullanılamaz
     */
    @Column(nullable = false)
    private boolean revoked = false;

    /*
     * Bir kullanıcı birden fazla Refresh Token'a sahip olabilir.
     * Örneğin; Telefon,Laptop,Tablet
       Bu nedenle ilişki ManyToOne'dır.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Boş constructor JPA tarafından zorunlu tutulur.
    public RefreshToken() {

    }

    public Long getId() {
        return id;
    }

    // ID veritabanı tarafından üretildiği için
    // normalde setter kullanılmayacaktır.
    public void setId(Long id) {
        this.id = id;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}