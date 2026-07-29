package com.demo.sprintw1.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public class UpdateDocumentRequest {

    @NotBlank(message = "Title cannot be blank.")
    private String title;

    private String description;

    // Yeni seçilen dosya.
    private MultipartFile file;

    public UpdateDocumentRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}