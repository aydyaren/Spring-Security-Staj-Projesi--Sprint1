package com.demo.sprintw1.service;

import com.demo.sprintw1.dto.CreateDocumentRequest;
import com.demo.sprintw1.dto.UpdateDocumentRequest;
import com.demo.sprintw1.entity.Document;
import com.demo.sprintw1.entity.User;
import com.demo.sprintw1.repository.DocumentRepository;
import com.demo.sprintw1.repository.UserRepository;

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

    public Document createDocument(CreateDocumentRequest request) {

        User owner = getCurrentUser();

        String fileName = fileStorageService.saveFile(request.getFile());

        Document document = new Document();
        document.setTitle(request.getTitle());
        document.setDescription(request.getDescription());
        document.setOwner(owner);

        document.setFileName(fileName);

        if (fileName != null) {
            document.setFilePath("uploads/" + fileName);
        }


        return documentRepository.save(document);
    }

    public List<Document> getAllDocuments() {

        User currentUser = getCurrentUser();

        String role = currentUser.getRole().getName();

        if (role.equals("ADMIN") || role.equals("MANAGER")) {
            return documentRepository.findAll();
        }

        return documentRepository.findByOwner(currentUser);
    }

    public Document getDocumentById(Long id) {

        Document document = documentRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Document not found"
                        )
                );

        checkOwnership(document);

        return document;
    }

    public Document updateDocument(Long id, UpdateDocumentRequest request) {

        Document document = documentRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Document not found"
                        )
                );

        checkOwnership(document);

        if (request.getTitle() != null) {
            document.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            document.setDescription(request.getDescription());
        }

        return documentRepository.save(document);
    }

    public void deleteDocument(Long id) {

        Document document = documentRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Document not found"
                        )
                );

        checkOwnership(document);

        documentRepository.delete(document);
    }

    private User getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found"
                        )
                );
    }

    private void checkOwnership(Document document) {

        User currentUser = getCurrentUser();

        String role = currentUser.getRole().getName();

        // ADMIN ve MANAGER her dokümana erişebilir.
        if (role.equals("ADMIN") || role.equals("MANAGER")) {
            return;
        }

        // EMPLOYEE sadece kendi dokümanına erişebilir.
        if (!document.getOwner().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to access this document"
            );
        }
    }
}