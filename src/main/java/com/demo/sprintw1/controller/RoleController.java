package com.demo.sprintw1.controller;

import com.demo.sprintw1.entity.Role;
import com.demo.sprintw1.service.RoleService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // Bu sınıf HTTP isteklerini karşılayacak.
@RequestMapping("/roles") // Controller'ın adresi /roles olsun. POST /roles isteği buraya gelecek.

public class RoleController {
    private final RoleService roleService;
    // Controller doğrudan Repository ile konuşmuyor.Her zaman önce Service'e gider.

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping // POST /roles isteği geldiğinde bu metod çalışacak.
    public Role createRole (@RequestBody Role role){
        return roleService.createRole(role);

    }
    /*
    bir HTTP isteğinin (POST, PUT vb.) gövdesinde (body) gönderilen karmaşık veriyi
    (genellikle JSON formatında) doğrudan bir Java objesine dönüştürmek için kullanılan bir anotasyondur.
    @RequestBody yazmazsak Spring, JSON'u otomatik olarak Role nesnesine çevirmeye çalışmaz.
    @RequestBody, Postman'dan gelen JSON verisini Role nesnesine dönüştürür. Böylece metodun içinde normal
    bir Java nesnesiyle çalışabiliriz.
     */
}
