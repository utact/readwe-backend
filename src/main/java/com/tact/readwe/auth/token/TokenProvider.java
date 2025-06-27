package com.tact.readwe.auth.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class TokenProvider {
    private static final Logger log = LoggerFactory.getLogger(TokenProvider.class);

    private final Key key;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public TokenProvider(JwtProperties properties) {
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes());
        this.accessTokenExpirationMs = properties.getAccessTokenExpiration();
        this.refreshTokenExpirationMs = properties.getRefreshTokenExpiration();
    }

    public String generateAccessToken(UUID userId, String email) {
        return generateToken(userId.toString(), email, "access", accessTokenExpirationMs);
    }

    public String generateRefreshToken(UUID userId, String email) {
        return generateToken(userId.toString(), email, "refresh", refreshTokenExpirationMs);
    }

    private String generateToken(String subjectId, String email, String type, long expirationTimeMs) {
        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .setSubject(subjectId)
                .claim("email", email)
                .claim("type", type)
                .setId(jti)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenExpired(String token) {
        try {
            getClaims(token);
            return false;
        } catch (ExpiredJwtException e) {
            return true;
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            log.warn("토큰 만료 여부 확인 중 다른 유형의 오류 발생: {}", e.getMessage());
            return false;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            log.warn("유효하지 않은 JWT 서명: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("유효하지 않은 JWT 토큰: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("비어 있는 JWT 클레임 문자열: {}", e.getMessage());
        }
        return false;
    }

    public UUID getUserIdFromToken(String token) {
        return UUID.fromString(getClaims(token).getSubject());
    }

    public String getEmailFromToken(String token) {
        return getClaims(token).get("email", String.class);
    }

    public String getTypeFromToken(String token) {
        return getClaims(token).get("type", String.class);
    }

    public String getJtiFromToken(String token) {
        return getClaims(token).getId();
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaims(token).getExpiration();
    }
}
