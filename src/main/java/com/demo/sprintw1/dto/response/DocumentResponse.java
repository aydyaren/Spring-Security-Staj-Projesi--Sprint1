package com.demo.sprintw1.dto.response;

import java.time.LocalDateTime;

public class DocumentResponse {

    private Long id;

    private String title;

    private String description;

    private String fileName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String ownerUsername;

    public DocumentResponse() {
    }

    public DocumentResponse(Long id,
                            String title,
                            String description,
                            String fileName,
                            LocalDateTime createdAt,
                            LocalDateTime updatedAt,
                            String ownerUsername) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.fileName = fileName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.ownerUsername = ownerUsername;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public void setOwnerUsername(String ownerUsername) {
        this.ownerUsername = ownerUsername;
    }
}
