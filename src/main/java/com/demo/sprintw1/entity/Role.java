package com.demo.sprintw1.entity;

import jakarta.persistence.*; //jakarta.persistence paketindeki bütün class ve annotation'ları içe aktar.

@Entity // Bu sınıfın veritabanında bir tablo olarak yönetileceğini belirtir.
@Table(name = "roles") // Veritabanındaki tablo adını belirler.
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // name boş bırakılamaz, ve aynı isimlendirme yapılamaz.
    private String name;

    public Role() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
