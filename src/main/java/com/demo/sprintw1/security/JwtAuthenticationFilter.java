package com.demo.sprintw1.security;

import com.demo.sprintw1.service.CustomUserDetailsService;
import com.demo.sprintw1.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/*Spring'in yönettiği nesne.@Component da Spring'e diyor ki:Bu sınıfı da Bean olarak yönet.Böylece SecurityConfig
içinde bunu injection edebileceğiz.*/
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    //Constructor Injection
    public JwtAuthenticationFilter(JwtService jwtService,
                                   CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response); /*Sıradaki filtreye veya controller'a devam et.
            Eğer bunu yazmazsak istek burada durur.Controller'a hiç gitmez.*/
            return; //Filtreyi bitiriyoruz.

        }

        String jwt = authHeader.substring(7); /*Bearer xxxxxx kısmında "Bearer " toplam 7 karakterdir.
        substring(7) ile Bearer kısmını atıp sadece JWT'yi alıyoruz.Artık elimizde sadece JWT String'i var.*/

        //JWT'nin kime ait olduğunu öğrenebilmek için email'i çıkartıyoruz.
        String email = jwtService.extractEmail(jwt);

        /*Email varsa ve SecurityContext içinde daha önce giriş yapmış bir kullanıcı yoksa
        doğrulama işlemine devam ediyoruz.*/
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            //Email'e göre kullanıcıyı veritabanından getiriyoruz.
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            //JWT gerçekten bu kullanıcıya mı ait ve süresi dolmamış mı kontrol ediyoruz.
            if (jwtService.isTokenValid(jwt, userDetails)) {

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                /*
                Spring Security'nin anlayacağı bir nesne oluşturuyoruz.İçinde şunlar var: UserDetails ,Email ,Authority
                (Role)
                 */

                /*Request ile ilgili detayları authentication nesnesine ekliyoruz.
                (IP adresi, Session bilgisi vb.)*/
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                /*
                Bu satır:IP adresi , Session bilgisi (varsa) , Request bilgileri gibi ek detayları Spring'e veriyor.
                 */

                /*Artık Spring Security'ye diyoruz ki:
                Bu kullanıcı giriş yaptı.Bundan sonraki bütün işlemlerde bu kullanıcıyı kullan.Bu kullanıcı artık
                doğrulandı.Bundan sonra herhangi bir Controller içinde Spring artık kullanıcıyı biliyor.*/
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            /*
            SecurityContexHolde : Spring Security'nin merkezi hafızası.
            getContext() : SecurityContext'i getir.
            setAuthentication(authenticationToken): Bu kullanıcı artık giriş yaptı.
             */
        }

        //Filtrede işimiz bitti.Controller'a devam ediyoruz.
        filterChain.doFilter(request, response);
    }
}