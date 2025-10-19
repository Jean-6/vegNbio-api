package org.example.vegnbioapi.security.auth;


import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${application.security.jwt.secret-key}")
    private String jwtSecretKey;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;


    public Key getSigninKey() {
        byte[] keyBytes = jwtSecretKey.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public String generateJwtToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigninKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigninKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("JWT expiré : " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("JWT non supporté : " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("JWT mal formé : " + e.getMessage());
        } catch (SignatureException e) {
            System.err.println("Signature JWT invalide : " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("JWT vide : " + e.getMessage());
        }
        return false;

    }
}


