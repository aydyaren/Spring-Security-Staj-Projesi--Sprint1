package com.demo.sprintw1.repository;

import com.demo.sprintw1.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmailOrUsername(String email, String username);

    @Query("""
        SELECT u
        FROM User u
        JOIN FETCH u.role
        WHERE u.id = :id
        """)
    Optional<User> findByIdWithRole(Long id);


    @Query("""
        SELECT u
        FROM User u
        JOIN FETCH u.role
        WHERE u.email = :email
        """)
    Optional<User> findByEmailWithRole(String email);



}



// findByEmail(): Users tablosundaki ilgili e-mail adresine sahip kullanıyıcı getir/bul .
// (String email): Kullanıcının yazdığı e-mail.

/*
NullPointerException (NPE):  hatanın genellikle boş bir nesne üzerinde metot çağrıldığında veya bir dizinin
boş bir öğesine erişilmeye çalışıldığında ortaya çıkar.

--> Hatalı Kullanım (NPE verir)
String metin = null;
int uzunluk = metin.length(); "metin" null olduğu için patlar

--> Doğru Kullanım
String metin = null;
if (metin != null) {
int uzunluk = metin.length();
}
*/

/*
Optional: veritabanında kullanıcı bulunamadığında null dönmesi yerine güvenli bir sonuç döndürür.
Böylece NullPointerException gibi hataların önüne geçilir.
*/