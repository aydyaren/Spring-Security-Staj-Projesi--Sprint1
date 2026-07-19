package com.demo.sprintw1.controller;

import com.demo.sprintw1.dto.CreateDocumentRequest;
import com.demo.sprintw1.dto.UpdateDocumentRequest;
import com.demo.sprintw1.entity.Document;
import com.demo.sprintw1.service.DocumentService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestParam;


    @RestController
@RequestMapping("/documents")
//Bu controller'ın bütün endpoint'leri artık documents ile başlayacak.
public class DocumentController {

    private final DocumentService documentService;

    //Dependency Injection kullanıyoruz.Spring bizim yerimize DocumentService nesnesini oluşturup buraya veriyor.
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }


    /*@PostMapping
    public Document createDocument(@Valid @ModelAttribute CreateDocumentRequest request) {
        return documentService.createDocument(request);
    }*/
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Document createDocument(
            @Valid @ModelAttribute CreateDocumentRequest request) {

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
            @Valid @ModelAttribute UpdateDocumentRequest request) {

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