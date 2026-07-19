package com.demo.sprintw1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveFile(MultipartFile file) {

        // Dosya yüklenmemişse hiçbir işlem yapma.
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {

            // uploads klasörünü temsil eder.
            Path uploadPath = Paths.get(uploadDir);

            // Klasör yoksa oluştur.
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Kullanıcının yüklediği dosyanın adı.
            String originalFileName = file.getOriginalFilename();

            // Benzersiz dosya adı oluştur.
            String uniqueFileName =
                    UUID.randomUUID() + "-" + originalFileName;

            // Dosyanın kaydedileceği tam yolu oluştur.
            Path targetLocation = uploadPath.resolve(uniqueFileName);

            // Dosyayı uploads klasörüne kopyala.
            Files.copy(
                    file.getInputStream(),
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );

            // Kaydedilen dosyanın adını döndür.
            return uniqueFileName;

        } catch (IOException e) {
            throw new RuntimeException("Could not store file.", e);
        }
    }
}