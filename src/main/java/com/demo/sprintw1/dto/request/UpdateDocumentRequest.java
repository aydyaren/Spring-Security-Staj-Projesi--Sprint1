package com.demo.sprintw1.dto.request;

import jakarta.validation.constraints.NotBlank;

public class UpdateDocumentRequest {

    @NotBlank(message = "Title boş olamaz")
    private String title;

    private String description;

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
}
