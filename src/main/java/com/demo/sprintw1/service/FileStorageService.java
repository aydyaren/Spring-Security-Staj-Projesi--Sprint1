package com.demo.sprintw1.service;

import com.demo.sprintw1.exception.InvalidFileException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveFile(MultipartFile file) {

        // Dosya yüklenmemişse hiçbir işlem yapma.
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Maksimum dosya boyutu: 10 MB
        long maxFileSize = 10 * 1024 * 1024;

        if (file.getSize() > maxFileSize) {
            throw new InvalidFileException("File size exceeds 10 MB.");
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

            if (originalFileName == null) {
                throw new InvalidFileException("File name not found.");
            }

            // Uzantısız dosya yüklenmesini engelliyoruz.
            if (!originalFileName.contains(".")) {
                throw new InvalidFileException("File extension not found.");
            }

            // Dosya uzantısını alıyoruz.
            String extension = originalFileName
                    .substring(originalFileName.lastIndexOf('.') + 1)
                    .toLowerCase();

            // Sadece bu uzantılara izin veriyoruz.
            List<String> allowedExtensions = List.of(
                    "pdf",
                    "jpg",
                    "jpeg",
                    "png",
                    "docx"
            );

            if (!allowedExtensions.contains(extension)) {
                throw new InvalidFileException("Unsupported file type.");
            }

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

    public Resource loadFile(String filePath) {

        try {

            // String'i Path'e dönüştürüyor.
            Path path = Paths.get(filePath);

            Resource resource = new UrlResource(path.toUri());

            // File silindiyse 500 dönmemek için not found fırlatıyoruz.
            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("File not found.");
            }

            // Service bu dosyayı Controller'a verebilir.
            // Controller da bunu HTTP Response olarak kullanıcıya gönderecek.
            return resource;

        } catch (MalformedURLException e) {

            throw new RuntimeException("File not found.", e);

        }
    }

    public void deleteFile(String filePath) {

        try {

            // Database'de duran uploads/63482888-f7cb-4fde-images.jpg
            // String'ini tekrar gerçek bir Path nesnesine çeviriyoruz.
            Path path = Paths.get(filePath);

            // Dosya varsa sil. Yoksa hata verme.
            Files.deleteIfExists(path);

            /*
             Disk işlemlerinde her zaman hata olabilir.
             Örneğin:
             - Dosya kilitlidir.
             - İzin yoktur.
             - Diskte sorun vardır.

             Bu yüzden IOException yakalıyoruz.
            */
        } catch (IOException e) {

            throw new RuntimeException("File could not be deleted.", e);

        }

    }
}