package com.tact.readwe.auth.token;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class TokenValidator {
    private static final Logger log = LoggerFactory.getLogger(TokenValidator.class);
    private final Key key;

    public TokenValidator(JwtProperties properties) {
        this.key = Keys.hmacShaKeyFor(properties.getSecret().getBytes());
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT token", e);
            return false;
        }
    }

    public boolean isExpired(String token) {
        try {
            Date expiration = parse(token).getExpiration();
            return expiration.before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return true;
        }
    }

    private Claims parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
