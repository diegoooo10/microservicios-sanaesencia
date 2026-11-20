
package com.sanaesencia.reserva.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Value("${app.internal.key}")
    private String internalKey;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(auth -> auth

                        // Documentación Swagger
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Comunicación entre microservicios
                        .requestMatchers("/api/interno/reservas/**")
                        .hasRole("INTERNAL")

                        // Solicitudes de clientes autenticados
                        .requestMatchers("/api/reservas/**")
                        .hasRole("CLIENTE")

                        // Denegar cualquier otra solicitud
                        .anyRequest().denyAll()
                )
                .addFilterBefore(
                        internalKeyFilter(),
                        UsernamePasswordAuthenticationFilter.class
                )
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }

    @Bean
    OncePerRequestFilter internalKeyFilter() {

        return new OncePerRequestFilter() {

            @Override
            protected boolean shouldNotFilter(
                    HttpServletRequest request
            ) {
                return !request.getServletPath()
                        .startsWith("/api/interno/reservas/");
            }

            @Override
            protected void doFilterInternal(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    FilterChain filterChain
            ) throws ServletException, IOException {

                String receivedKey = request.getHeader("X-Internal-Key");

                boolean valid = receivedKey != null
                        && !internalKey.isBlank()
                        && MessageDigest.isEqual(
                                internalKey.getBytes(StandardCharsets.UTF_8),
                                receivedKey.getBytes(StandardCharsets.UTF_8)
                        );

                if (!valid) {
                    response.sendError(
                            HttpServletResponse.SC_UNAUTHORIZED,
                            "Acceso interno no autorizado"
                    );
                    return;
                }

                var authentication =
                        new UsernamePasswordAuthenticationToken(
                                "pago-service",
                                null,
                                List.of(
                                        new SimpleGrantedAuthority(
                                                "ROLE_INTERNAL"
                                        )
                                )
                        );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

                filterChain.doFilter(request, response);
            }
        };
    }
}
