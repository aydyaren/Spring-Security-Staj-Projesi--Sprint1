package com.demo.sprintw1.security;

import com.demo.sprintw1.config.RateLimitConfig;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // Rate limit ayarlarını kullandığımız sınıf.
    private final RateLimitConfig rateLimitConfig;

    // Constructor Injection kullanıyoruz.
    public RateLimitFilter(RateLimitConfig rateLimitConfig) {
        this.rateLimitConfig = rateLimitConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Sadece login endpoint'ini kontrol ediyoruz.
        if (request.getRequestURI().equals("/auth/login")
                && request.getMethod().equalsIgnoreCase("POST")) {

            // İstek atan kullanıcının IP adresini alıyoruz.
            String ipAddress = request.getRemoteAddr();

            // Bu IP adresine ait bucket'ı alıyoruz.
            Bucket ipBucket = rateLimitConfig.resolveIpBucket(ipAddress);

            // Bucket'tan 1 token tüketmeye çalışıyoruz.
            if (!ipBucket.tryConsume(1)) {

                // Limit aşıldığında 429 hatası döndürüyoruz.
                response.setStatus(429);

                // Dönecek verinin Json olduğunu belirtiyoruz.
                response.setContentType("application/json");

                // İstemciye tekrar ne zaman deneyebileceğini bildiriyoruz.
                response.setHeader("Retry-After", "60");

                // Kullanıcıya anlamlı bir hata mesajı gönderiyoruz.
                response.getWriter().write("""
                        {
                          "message": "Too many requests from this IP. Please try again in one minute."
                        }
                        """);

                return;
            }
        }

        // Limit aşılmadıysa istek normal şekilde devam ediyor.
        filterChain.doFilter(request, response);
    }
}