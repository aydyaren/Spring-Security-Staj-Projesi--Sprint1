package com.demo.sprintw1.service;

import com.demo.sprintw1.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    // SecretKey : JWT'nin imzasını oluşturacak gizli anahtar
    // Artık kaynak kodunda sabit (hardcoded) değil, application.properties
    // üzerinden (ve oradan da environment variable üzerinden) okunuyor.
    // Böylece secret Git repository'sine commit edilmiyor.
    private final SecretKey secretKey;

    // Token geçerlilik süresi de application.properties'ten okunuyor.
    private final long expirationMs;


    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs) {

        this.secretKey = Keys.hmacShaKeyFor( //Bu metod bize güvenli bir Secret Key oluşturuyor.
                secret.getBytes(StandardCharsets.UTF_8)
        );
        this.expirationMs = expirationMs;
    }

    // JWT oluştur

    public String generateToken(User user) {
        return Jwts.builder() //Builder Pattern
                .subject(user.getEmail()) // token'ın sahibi kim? Kimin e-mail ve role verilerini alıyoruz?
                .claim("role", user.getRole().getName()) //Burada Payload'a kendi alanımızı ekliyoruz
                .issuedAt(new Date())  //Bu token şu anda oluşturuldu.
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                //token application.properties'te tanımlı süre kadar geçerli olsun.
                .signWith(secretKey) //Signature burada oluşuyor.
                .compact();
                /*
                Builder Pattern'i bitiriyor ve sonunda bize String döndürüyor.Exp:eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
                 */
    }

    //JWT içinden E-mail'i çıkartma.

    public String extractEmail(String token) {

        return extractClaim(token, Claims::getSubject);
    }

    // İstenilen Claim'i çıkar

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {

        Claims claims = extractAllClaims(token);

        return resolver.apply(claims);
    }

    // Bütün Claim'leri oku

    private Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Süresi dolmuş mu?
    public boolean isTokenExpired(String token) {

        return extractClaim(token, Claims::getExpiration)
                .before(new Date());
    }

    // Token geçerli mi?
    public boolean isTokenValid(String token, UserDetails userDetails) {

        String email = extractEmail(token);

        return email.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

}