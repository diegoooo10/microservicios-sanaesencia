package com.sanaesencia.pago.config;

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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain security(HttpSecurity http, InternalKeyFilter internalKeyFilter) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/pagos/webpay/retorno").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(internalKeyFilter,
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    InternalKeyFilter internalKeyFilter(@Value("${app.internal.api-key}") String apiKey) {
        return new InternalKeyFilter(apiKey);
    }

    static class InternalKeyFilter extends OncePerRequestFilter {
        private final byte[] expected;

        InternalKeyFilter(String apiKey) {
            if (apiKey == null || apiKey.length() < 24 || apiKey.startsWith("CAMBIAR")) {
                throw new IllegalStateException("Configura app.internal.api-key con una clave de al menos 24 caracteres");
            }
            this.expected = apiKey.getBytes(StandardCharsets.UTF_8);
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                        FilterChain chain) throws ServletException, IOException {
            String provided = request.getHeader("X-Internal-Key");
            if (provided != null && MessageDigest.isEqual(expected,
                    provided.getBytes(StandardCharsets.UTF_8))) {
                var authentication = new UsernamePasswordAuthenticationToken(
                        "backend-service", null, List.of(() -> "ROLE_INTERNAL"));
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            chain.doFilter(request, response);
        }
    }
}
