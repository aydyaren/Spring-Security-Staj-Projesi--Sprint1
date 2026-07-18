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

        System.out.println("===== JWT FILTER =====");
        System.out.println("PATH = " + request.getRequestURI());
        System.out.println("HEADER = " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);

        System.out.println("JWT = " + jwt);

        try {
            String email = jwtService.extractEmail(jwt);

            System.out.println("EMAIL = " + email);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                System.out.println("TOKEN VALID");

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtService.isTokenValid(jwt, userDetails)) {

                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                } else {
                    System.out.println("TOKEN INVALID");
                }
            }

        } catch (Exception e) {
            System.out.println("JWT parse hatası: " + e.getMessage());
            // token geçersizse authentication set edilmez, controller'a devam edilir
            // (endpoint zaten authenticated() istiyorsa yine 401 döner ama en azından
            // stack trace loglarınızı kirletmez)
        }

        // Filtrede işimiz bitti. Controller'a devam ediyoruz.
        filterChain.doFilter(request, response);
    }
}