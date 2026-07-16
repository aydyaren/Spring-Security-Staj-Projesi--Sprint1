package com.demo.sprintw1.config;


import com.demo.sprintw1.service.CustomUserDetailsService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

@Configuration
public class SecurityConfig {

    @Bean
    /*public SecurityFilterChain securityFilterChain(HttpSecurity http)
    Spring burada HttpSecurity'yi verdi.
     */
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                /*.authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() Gelen bütün HTTP isteklerine izin verir. Herkes bütün endpoint'lere
                        erişebilir. Bu yüzden
                )*/

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login").permitAll() /*/auth/login endpoint'ine herkes erişebilir.Rol
                kontrolleri login olduktan sonra yapılır.Bu endpoint'i korusaydık kullanıcının login yapması mümkün
                olmazdı.Token kısmına loginden sonra ihtiyacımız var.*/

                        .anyRequest().authenticated()
                        //Bunun dışındaki bütün endpoint'ler için kullanıcı giriş yapmış olmalı.
                )
                //Burada henüz ; ADMIN , MANAGER ,EMPLOYEE yok.Sadece: "Login olmuş mu?" sorusuna bakıyor.


                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() { /* Spring'e bir PasswordEncoder nesnesi oluşturmasını ve
         gerektiğinde geri vermesini söyler.Yani bu aşamada henüz hiçbir kullanıcı şifresi değişmeyecek.*/
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider( /*Spring'e diyoruz ki:AuthenticationProvider
                                                           Bean'ini ben oluşturacağım.*/
            CustomUserDetailsService customUserDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(); //Provider oluşturuyoruz.
        /*
        DaoAuthenticationProvider, veritabanı (DAO = Data Access Object) üzerinden
        kullanıcı doğrulayan Spring Security'nin hazır implementasyonudur.
         */

        provider.setUserDetailsService(customUserDetailsService); /*UserDetailsService'i veriyoruz.Bunun anlamı:
        Kullanıcıyı bulmak istediğinde benim yazdığım CustomUserDetailsService'i kullan. */

        provider.setPasswordEncoder(passwordEncoder); /*PasswordEncoder'ı veriyoruzBunun anlamı:Şifreleri
        karşılaştırırken BCrypt kullan."Yani login sırasında Spring Security kendi içinde buna benzer bir şey yapacak:
        passwordEncoder.matches(
        kullanıcınınGirdiğiŞifre,
        veritabanındakiHash
        ); */

        return provider; //Spring Container'a bu nesneyi veriyoruz.
    }

    @Bean
    public AuthenticationManager authenticationManager(
            //Spring bize diyor ki:Authentication yapılandırmasını ben hazırladım.Dependency Injection kullanıyoruz.
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
        /*
        Spring'den hazırladığı AuthenticationManager'ı bize vermesini istiyoruz. Biz üretmiyoruz.Biz sadece
        Spring'den alıyoruz.
        Başka bir açıdan -->bu nesneyi Spring Container'a tekrar Bean olarak koyuyoruz ki ileride istediğimiz
        yerde kullanabilelim.
         */

    }


}

/*.anyRequest().permitAll() --> Gelen bütün isteklere izin ver. Bu şekilde olursa sistemde employee olarak kayıtlı
bir kişi bir postman açıp DELETE /users/5 derse uygulama bu kullanıyıc siler. Bu da dışarıdan sisteme müdahale
edilebileceği için sistemde güvenlik açığı demektir.
 */

/*
Hash, bir verinin geri döndürülemeyecek şekilde dönüştürülmesidir.
Mesela kullanıcının şifresi:123456
BCrypt bunu alıyor ve şuna benzer bir değere çeviriyor:$2a$10$kZhquDLW2y1hzetOjLRef.3Dyw5.oXCcdEoVKRVWquhJOiTPtVdaq
Buna hash denir. Hash bir şifreleme(encryption) değildir.
 */

/*
SecurityConfig

├── PasswordEncoder
├── AuthenticationProvider
└── AuthenticationManager

Bu üçlü olmadan login sistemi kurulamaz.
 */

/*
/auth/login
        ▼
Herkes girebilir
↓
AuthenticationManager
↓
Email + Password doğru mu?
↓
Evet
↓
JWT oluştur
↓
Artık token var
↓
Diğer endpointlere erişebilir
 */

