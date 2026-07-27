package com.demo.sprintw1.service;

import com.demo.sprintw1.config.RateLimitConfig;
import com.demo.sprintw1.exception.TooManyRequestsException;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

@Service
public class LoginRateLimitService {

    // Rate limit ayarlarını kullandığımız sınıf.
    private final RateLimitConfig rateLimitConfig;

    // Constructor Injection kullanıyoruz.
    public LoginRateLimitService(RateLimitConfig rateLimitConfig) {
        this.rateLimitConfig = rateLimitConfig;
    }

    // Login (username veya e-mail) için rate limit kontrolü yapıyoruz.
    public void checkLoginLimit(String login) {

        // Login bilgisine ait bucket'ı alıyoruz.
        Bucket loginBucket =
                rateLimitConfig.resolveLoginBucket(login);

        // Bucket'tan 1 token tüketmeye çalışıyoruz.
        if (!loginBucket.tryConsume(1)) {

            // Limit aşılmışsa hata fırlatıyoruz.
            throw new TooManyRequestsException(
                    "Too many login attempts for this account. Please try again in one minute."
            );
        }
    }
}