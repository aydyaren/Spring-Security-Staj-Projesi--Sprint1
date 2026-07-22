package com.demo.sprintw1.dto.request;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;



public class CreateDocumentRequest {

    //Kullanıcı title girmek zorunda. " " , null , "" , gibi boşlukları reddeder.
    @NotBlank(message = "Title cannot be blank.")


    private String title;

    //Buraya validation koymadık. Çünkü açıklama zorunlu olmayabilir.
    private String description;

    // Kullanıcının yüklediği dosya (isteğe bağlı)
    private MultipartFile file;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}

/*
fileName = yüklenen dosyanın adı
filePath = dosyanın kaydedildiği yer
Kullanıcı bunları bilemez ve belirlememelidir.O yüzden burada fileName ve filePath istemiyoruz.Bu bilgileri kullanıcı
değil sistem oluşturuyor.
 */
