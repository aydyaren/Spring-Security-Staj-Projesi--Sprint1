package com.demo.sprintw1.service;

import com.demo.sprintw1.entity.Role;
import com.demo.sprintw1.repository.RoleRepository;
import org.springframework.stereotype.Service;

//import java.util.List;

@Service // Bu sınıf bus logic kurallarını içerir.
public class RoleService {

    private final RoleRepository roleRepository;
    /* roleRepository --> RoleService'in veritabanına erişebilmesi için RoleRepository'ye ihtiyacı var
    Burdaki roleRepository RoleRepository.java nesnesine ulaşabilmemiz/nesnesini referanslayabilmemiz
    için bu class içinde kullandığımız değişkendir.
    */

    /* final --> Bir kere değer verildikten sonra bu değişken başa bir şey gösteremez.
    Neden ihtiyaç duyuyoruz? -->  Service'ın kullandığı repository'nin sonradan değişmesini istemeyiz.
    Önemli --> İlk oluşturulduğunda mutlaka değer almalı.
     */

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    } // Constructor
    /*
    Spring uygulama başlarken RoleRepository nesnesini oluşturur ve bu constructor'a verir.
    Dependency Injection'ın en temel örneği.
     */
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }
    /*
    Controller --> RoleService.createRole(role) --> roleRepository.save(role) --> PostgreSQL
    roleRepository.save(role); --> Veritabanına kaydeder.
    return roleRepository.save(role); --> Veritabanına kaydedilmiş son halini geri döndürür.
     */

    /*public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    Şu an için ihityacımız yok ama frontend ksımında rolleri görmek istersek bunu kullanabiliriz.
    Yukarıda -import java.util.List;- kısmı var orayı da yorum olarak düzelttik */

}