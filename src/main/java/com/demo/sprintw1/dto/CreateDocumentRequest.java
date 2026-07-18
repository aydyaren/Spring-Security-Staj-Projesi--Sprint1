package com.demo.sprintw1.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateDocumentRequest {

    //Kullanıcı title girmek zorunda. " " , null , "" , gibi boşlukları reddeder.
    @NotBlank(message = "Title cannot be blank.")

    //Buraya validation koymadık. Çünkü açıklama zorunlu olmayabilir.
    private String title;


    private String description;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

/*
fileName = yüklenen dosyanın adı
filePath = dosyanın kaydedildiği yer
Kullanıcı bunları bilemez ve belirlememelidir.O yüzden burada fileName ve filePath istemiyoruz.Bu bilgileri kullanıcı
değil sistem oluşturuyor.
 */
