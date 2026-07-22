package com.demo.sprintw1.service;

import com.demo.sprintw1.audit.AuditAction;
import com.demo.sprintw1.audit.AuditLog;
import com.demo.sprintw1.audit.AuditResource;
import com.demo.sprintw1.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final HttpServletRequest request;

    public AuditLogService(AuditLogRepository auditLogRepository,
                           HttpServletRequest request) {

        this.auditLogRepository = auditLogRepository;
        this.request = request;
    }

    // Authentication işlemleri (LOGIN / LOGOUT / REFRESH_TOKEN)
    // Kullanıcının email bilgisi zaten elimizde olduğu için parametre olarak alıyoruz.
    public void saveLog(String userEmail,
                        AuditAction action,
                        AuditResource resource,
                        Long resourceId) {

        AuditLog auditLog = new AuditLog();

        // IP adresini alıyoruz.
        String ipAddress = getClientIpAddress();

        // User-Agent bilgisini alıyoruz.
        String userAgent = request.getHeader("User-Agent");

        auditLog.setUserEmail(userEmail);
        auditLog.setAction(action);
        auditLog.setResource(resource);
        auditLog.setResourceId(resourceId);
        auditLog.setIpAddress(ipAddress);
        auditLog.setUserAgent(userAgent);

        auditLogRepository.save(auditLog);
    }

    // Diğer işlemler (Document vb.) için giriş yapan kullanıcıyı
    // SecurityContext üzerinden alıyoruz.
    public void saveLog(AuditAction action,
                        AuditResource resource,
                        Long resourceId) {

        // Giriş yapan kullanıcının email bilgisini alıyoruz.
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String userEmail = authentication.getName();

        saveLog(userEmail, action, resource, resourceId);
    }

    // İstemcinin gerçek IP adresini alıyoruz.
    // Proxy arkasında çalışıyorsa X-Forwarded-For kullanılır.
    // Aksi halde doğrudan isteği gönderen IP alınır.
    private String getClientIpAddress() {

        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}