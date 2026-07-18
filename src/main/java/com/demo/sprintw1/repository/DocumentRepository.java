package com.demo.sprintw1.repository;

import com.demo.sprintw1.entity.Document;
//Bu repository'nin yöneteceği entity'yi söylüyoruz.Yani artık bu repository sadece D ocument ile çalışacak.

import org.springframework.data.jpa.repository.JpaRepository;
import com.demo.sprintw1.entity.User;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByOwner(User owner);

}




