package com.demo.sprintw1.controller;

import com.demo.sprintw1.dto.CreateDocumentRequest;
import com.demo.sprintw1.dto.UpdateDocumentRequest;
import com.demo.sprintw1.entity.Document;
import com.demo.sprintw1.service.DocumentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/documents")
//Bu controller'ın bütün endpoint'leri artık documents ile başlayacak.
public class DocumentController {

    private final DocumentService documentService;

    //Dependency Injection kullanıyoruz.Spring bizim yerimize DocumentService nesnesini oluşturup buraya veriyor.
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping
    public Document createDocument(@Valid @RequestBody CreateDocumentRequest request) {
        return documentService.createDocument(request);
    }

    
    @GetMapping
    public List<Document> getAllDocuments() {
        return documentService.getAllDocuments();
    }
    //Artık bu endpoint'e: ADMIN ve MANAGER girebilir. EMPLOYEE giremez (403 Forbidden alır).

    @GetMapping("/test")
    public String test() {
        return "Controller çalıştı";
    }

    @PutMapping("/{id}")
    public Document updateDocument(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDocumentRequest request) {

        return documentService.updateDocument(id, request);
    }

    @GetMapping("/{id}")
    public Document getDocumentById(@PathVariable Long id) {
        return documentService.getDocumentById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) //İşlem başarılıysa 204 No Content döner.
    public void deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id); //Silme işlemini service'e bırakır.
    }





}

/*@RequestBody CreateDocumentRequest request :
HTTP isteğinin body kısmındaki JSON'u al ve bunu CreateDocumentRequest nesnesine dönüştür.*/