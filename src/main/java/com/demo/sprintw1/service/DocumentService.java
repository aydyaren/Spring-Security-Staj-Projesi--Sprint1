package com.demo.sprintw1.service;

import com.demo.sprintw1.dto.CreateDocumentRequest;
import com.demo.sprintw1.dto.UpdateDocumentRequest;
import com.demo.sprintw1.dto.response.DocumentDownloadResponse;
import com.demo.sprintw1.dto.response.DocumentResponse;
import com.demo.sprintw1.entity.Document;
import com.demo.sprintw1.entity.User;
import com.demo.sprintw1.repository.DocumentRepository;
import com.demo.sprintw1.repository.UserRepository;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public DocumentService(DocumentRepository documentRepository,
                           UserRepository userRepository,
                           FileStorageService fileStorageService) {

        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    public DocumentResponse createDocument(CreateDocumentRequest request) {

        User owner = getCurrentUser(); //Giriş yapan kullanıcıyı alıyoruz.

        // Artık saveFile hem disk adını hem orijinal adı birlikte döndürüyor.
        FileStorageResult storageResult = fileStorageService.saveFile(request.getFile());

        Document document = new Document();

        document.setTitle(request.getTitle());
        document.setDescription(request.getDescription());
        document.setOwner(owner);

        if (storageResult != null) {
            document.setFileName(storageResult.storedFileName());
            document.setOriginalFileName(storageResult.originalFileName());
            document.setFilePath("uploads/" + storageResult.storedFileName());
        }

        //Document'i veritabanına kaydediyoruz.
        Document savedDocument = documentRepository.save(document);

        //Entity yerine DTO döndürüyoruz.
        return mapToResponse(savedDocument);
    }

    public List<DocumentResponse> getAllDocuments() {

        User currentUser = getCurrentUser();

        String role = currentUser.getRole().getName();

        //ADMIN ve MANAGER bütün dokümanları görebilir (admin dokümanları dahil).
        if (role.equals("ADMIN") || role.equals("MANAGER")) {

            return documentRepository.findAll()
                    .stream() //Listeyi Stream'e dönüştürüyor.
                    .map(this::mapToResponse) //Her Document'i DocumentResponse'a dönüştürüyor.
                    .toList(); //Tekrar liste haline getiriyor.
        }

        //EMPLOYEE sadece kendi dokümanlarını görebilir.
        return documentRepository.findByOwner(currentUser)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public DocumentResponse getDocumentById(Long id) {

        Document document = documentRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Document not found"
                        )
                );

        //Görüntüleme yetkisi var mı kontrol ediyoruz (MANAGER admin dokümanını da görebilir).
        checkViewPermission(document);

        return mapToResponse(document);
    }

    public DocumentResponse updateDocument(Long id, UpdateDocumentRequest request) {

        Document document = documentRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Document not found"
                        )
                );

        //Güncelleme yapmaya yetkisi var mı kontrol ediyoruz (MANAGER admin dokümanını düzenleyemez).
        checkModifyPermission(document);

        //Sadece dolu gelen alanları güncelliyoruz.
        if (request.getTitle() != null) {
            document.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            document.setDescription(request.getDescription());
        }

        Document updatedDocument = documentRepository.save(document);

        return mapToResponse(updatedDocument);
    }

    public void deleteDocument(Long id) {

        Document document = documentRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Document not found"
                        )
                );

        //Silmeye yetkisi var mı kontrol ediyoruz (MANAGER admin dokümanını silemez).
        checkModifyPermission(document);

        //Önce fiziksel dosyayı siliyoruz.
        fileStorageService.deleteFile(document.getFilePath());

        //Sonra veritabanındaki kaydı siliyoruz.
        documentRepository.delete(document);
    }

    public DocumentDownloadResponse downloadDocument(Long id) {

        //Veritabanından Document'i çekiyoruz.
        Document document = documentRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Document not found"
                        )
                );

        //Dosyayı indirmeye yetkisi var mı kontrol ediyoruz (MANAGER admin dokümanını indiremez).
        checkModifyPermission(document);

        //Dosyayı uploads klasöründen okuyup Controller'a gönderiyoruz.
        Resource resource = fileStorageService.loadFile(document.getFilePath());

        // Kullanıcıya gösterilecek orijinal dosya adını da birlikte döndürüyoruz.
        return new DocumentDownloadResponse(resource, document.getOriginalFileName());
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        //JWT içerisindeki email bilgisini alıyoruz.
        String email = authentication.getName();

        //Email'e göre kullanıcıyı veritabanından çekiyoruz.
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        )
                );
    }

    // Sadece GÖRÜNTÜLEME için kullanılır: ADMIN ve MANAGER her dokümanı görebilir,
    // admin dokümanları da dahil. EMPLOYEE sadece kendi dokümanını görebilir.
    private void checkViewPermission(Document document) {

        User currentUser = getCurrentUser();

        String role = currentUser.getRole().getName();

        //ADMIN ve MANAGER her dokümanı görüntüleyebilir.
        if (role.equals("ADMIN") || role.equals("MANAGER")) {
            return;
        }

        //EMPLOYEE sadece kendi dokümanını görüntüleyebilir.
        if (!document.getOwner().getId().equals(currentUser.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to access this document"
            );
        }
    }

    // İNDİRME / DÜZENLEME / SİLME için kullanılır: ADMIN her şeyi yapabilir,
    // MANAGER admin dokümanları HARİÇ her şeyi yapabilir, EMPLOYEE sadece kendi dokümanında.
    private void checkModifyPermission(Document document) {

        User currentUser = getCurrentUser();

        String role = currentUser.getRole().getName();
        String ownerRole = document.getOwner().getRole().getName();

        //ADMIN her dokümanı indirebilir/düzenleyebilir/silebilir.
        if (role.equals("ADMIN")) {
            return;
        }

        //MANAGER, ADMIN'e ait dokümanlar hariç indirebilir/düzenleyebilir/silebilir.
        if (role.equals("MANAGER")) {

            if (ownerRole.equals("ADMIN")) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Managers are not allowed to download, edit or delete admin documents"
                );
            }

            return;
        }

        //EMPLOYEE sadece kendi dokümanını indirebilir/düzenleyebilir/silebilir.
        if (!document.getOwner().getId().equals(currentUser.getId())) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to access this document"
            );
        }
    }

    //Entity'yi API'nin döneceği DTO'ya dönüştürüyoruz.
    private DocumentResponse mapToResponse(Document document) {

        return new DocumentResponse(
                document.getId(),
                document.getTitle(),
                document.getDescription(),
                document.getFileName(),
                document.getCreatedAt(),
                document.getUpdatedAt(),
                document.getOwner().getUsername()
        );
    }
}