package com.demo.sprintw1.controller;

import com.demo.sprintw1.dto.CreateDocumentRequest;
import com.demo.sprintw1.dto.UpdateDocumentRequest;
import com.demo.sprintw1.service.DocumentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.demo.sprintw1.dto.response.DocumentResponse;
import com.demo.sprintw1.dto.response.DocumentDownloadResponse;


@RestController
@RequestMapping("/documents")
//Bu controller'ın bütün endpoint'leri artık documents ile başlayacak.
public class DocumentController {

    private final DocumentService documentService;

    //Dependency Injection kullanıyoruz.Spring bizim yerimize DocumentService nesnesini oluşturup buraya veriyor.
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentResponse createDocument(
            @Valid @ModelAttribute CreateDocumentRequest request) {

        return documentService.createDocument(request);
    }


    @GetMapping
    public List<DocumentResponse> getAllDocuments() {
        return documentService.getAllDocuments();

    }

    @GetMapping("/test")
    public String test() {
        return "Controller çalıştı";
    }

    @PutMapping("/{id}")
    public DocumentResponse updateDocument(
            @PathVariable Long id,
            @Valid @ModelAttribute UpdateDocumentRequest request) {

        return documentService.updateDocument(id, request);
    }

    @GetMapping("/{id}")
    public DocumentResponse getDocumentById(@PathVariable Long id) {
        return documentService.getDocumentById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) //İşlem başarılıysa 204 No Content döner.
    public void deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id); //Silme işlemini service'e bırakır.
    }

    @GetMapping("/{id}/download") //Endpoint.
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) {

        // Service artık hem dosyayı (Resource) hem de kullanıcıya
        // gösterilecek orijinal dosya adını birlikte döndürüyor.
        DocumentDownloadResponse download = documentService.downloadDocument(id);

        Resource resource = download.resource();

        // Content-Disposition header'ını manuel string birleştirme yerine
        // Spring'in builder'ıyla oluşturuyoruz; bu sayede orijinal dosya adında
        // özel karakter (", \r\n vb.) olsa bile header injection riski oluşmuyor.
        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(download.originalFileName(), StandardCharsets.UTF_8)
                .build();

        // Dosya uzantısına göre uygun Content-Type'ı otomatik belirliyoruz.
        MediaType mediaType = MediaTypeFactory.getMediaType(resource)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(resource);
    }




}

/*@RequestBody CreateDocumentRequest request :
HTTP isteğinin body kısmındaki JSON'u al ve bunu CreateDocumentRequest nesnesine dönüştür.*/