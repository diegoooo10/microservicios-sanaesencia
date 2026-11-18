
package com.sanaesencia.usuario.security;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sanaesencia.usuario.model.entity.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
public class JwtService {

    private final SecretKey signingKey;
    private final long expirationMs;
    private final long passwordResetMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs,
            @Value("${app.jwt.password-reset-ms}") long passwordResetMs
    ) {
        this.signingKey = Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );
        this.expirationMs = expirationMs;
        this.passwordResetMs = passwordResetMs;
    }

    public String generateToken(Usuario usuario) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(usuario.getId().toString())
                .claim("rol", usuario.getRol().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(expirationMs)))
                .signWith(signingKey)
                .compact();
    }


    public String generatePasswordResetToken(Usuario usuario) {
    Instant now = Instant.now();

    return Jwts.builder()
            .subject(usuario.getId().toString())
            .claim("purpose", "password_reset")
            .claim("passwordFingerprint", generarHuellaPassword(usuario))
            .id(UUID.randomUUID().toString())
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusMillis(passwordResetMs)))
            .signWith(signingKey)
            .compact();
}


    public Long validatePasswordResetToken(
        String token,
        Usuario usuario
) {

    Claims claims = validateAndGetClaims(token);

    String purpose = claims.get("purpose", String.class);

    if (!"password_reset".equals(purpose)) {
        throw new IllegalArgumentException(
                "Token de recuperación inválido"
        );
    }

    if (!usuario.getId().toString().equals(claims.getSubject())) {
        throw new IllegalArgumentException(
                "Token de recuperación inválido"
        );
    }

    String huellaToken = claims.get(
            "passwordFingerprint",
            String.class
    );

    String huellaActual = generarHuellaPassword(usuario);

    if (huellaToken == null ||
            !MessageDigest.isEqual(
                    huellaToken.getBytes(StandardCharsets.UTF_8),
                    huellaActual.getBytes(StandardCharsets.UTF_8)
            )) {

        throw new IllegalArgumentException(
                "El enlace de recuperación ya no es válido"
        );
    }

    return usuario.getId();
}



    public Claims validateAndGetClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    private String generarHuellaPassword(Usuario usuario) {

    try {
        Mac mac = Mac.getInstance("HmacSHA256");

        mac.init(new SecretKeySpec(
                signingKey.getEncoded(),
                "HmacSHA256"
        ));

        byte[] resultado = mac.doFinal(
                usuario.getPassword().getBytes(StandardCharsets.UTF_8)
        );

        return HexFormat.of().formatHex(resultado);

    } catch (Exception exception) {
        throw new IllegalStateException(
                "No se pudo generar la huella de seguridad",
                exception
        );
    }
}
}
