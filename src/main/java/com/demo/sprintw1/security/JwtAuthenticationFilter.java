package com.demo.sprintw1.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*Spring'in yönettiği nesne.@Component da Spring'e diyor ki:Bu sınıfı da Bean olarak yönet.Böylece SecurityConfig
içinde bunu injection edebileceğiz.*/
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response); /*sıradaki filtreye veya controller'a devam et.Eğer bunu yazmazsak
            ne olur? İstek burada durur.Controller'a hiç gitmez.*/
            return; //Filtreyi bitiriyoruz.

        }

        String jwt = authHeader.substring(7);/*Bearer_xxxxxx boşluk kısmına kadar 7 karakter var sonrasında
        JWT kısmı. Biz bu kısımda bearer ve jwt ayırarak jwt kısmını alıyoruz.Elimizde String Jwt kalıyor.*/

        //JWT'nin içinden email'i çıkarmamız herekiyor. O yüzden JwtService 'e gitmeliyiz.

    }
    //Burada henüz jwt doğrulaması yapmıyoruz , header var mı diye bakıyoruz.
    /*Extends bir sınıftan miras alır. Spring'in  hazır filtre sınıfını kullanıyoruz.*/

}


//OncePerRequestFilter : Her HTTP isteğinde (request) bu filtre yalnızca bir kez çalışır.

/*
request: Kullanıcının gönderdiği HTTP isteği.
getHeader("Authorization") : Header'ındaki Authorization değerini alıyor.
 */

/*
Kullanıcı Authorization header'ı göndermezse?
Mesela şöyle bir istek attı:GET /users
Hiç header yok.O zaman:authHeader --> null
Bunu kontrol etmek için if kullandık.
 */