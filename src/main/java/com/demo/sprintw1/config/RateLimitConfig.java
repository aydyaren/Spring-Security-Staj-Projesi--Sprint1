package com.demo.sprintw1.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig {

    // Her IP adresi için ayrı bucket oluşturuyoruz.
    private final Map<String, Bucket> ipBuckets = new ConcurrentHashMap<>();

    // Her login (username veya e-mail) için ayrı bucket oluşturuyoruz.
    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();

    // İstenen IP adresine ait bucket'ı döndürüyoruz.
    public Bucket resolveIpBucket(String ipAddress) {

        // Eğer bu IP daha önce istek attıysa mevcut bucket'ı kullanıyoruz.
        // Eğer ilk kez istek atıyorsa yeni bucket oluşturuyoruz.
        return ipBuckets.computeIfAbsent(ipAddress, this::createIpBucket);
    }

    // İstenen login değerine ait bucket'ı döndürüyoruz.
    public Bucket resolveLoginBucket(String login) {

        // Eğer bu login daha önce istek attıysa mevcut bucket'ı kullanıyoruz.
        // Eğer ilk kez istek atıyorsa yeni bucket oluşturuyoruz.
        return loginBuckets.computeIfAbsent(login.toLowerCase(), this::createLoginBucket);
    }

    // IP adresi için yeni bucket oluşturuyoruz.
    private Bucket createIpBucket(String key) {

        // Bir dakika içinde en fazla 100 login isteğine izin veriyoruz.
        Bandwidth limit = Bandwidth.classic(
                10,
                Refill.greedy(10, Duration.ofMinutes(1))
        );

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    // Login (username veya e-mail) için yeni bucket oluşturuyoruz.
    private Bucket createLoginBucket(String key) {

        // Aynı kullanıcı için bir dakika içinde en fazla 5 login denemesine izin veriyoruz.
        Bandwidth limit = Bandwidth.classic(
                5,
                Refill.greedy(5, Duration.ofMinutes(1))
        );

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}