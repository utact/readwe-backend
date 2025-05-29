package com.tact.readwe.auth.token;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class TokenProvider {
    private final Key key;
    private final long expirationMs;

    public TokenProvider(JwtProperties properties) {
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes());
        this.expirationMs = properties.getAccessTokenExpiration();
    }

    public String generateAccessToken(String userId, String email) {
        return generateToken(userId, Map.of(
                "email", email,
                "type", "access"
        ));
    }

    public String generateRefreshToken(String userId, String email) {
        return generateToken(userId, Map.of(
                "email", email,
                "type", "refresh"
        ));
    }

    private String generateToken(String subject, Map<String, Object> claims) {
        return Jwts.builder()
                .setSubject(subject)
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
