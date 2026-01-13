package com.oakey.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtProvider {

    private final SecretKey key;
    private final long accessExpSeconds;
    private final long refreshExpSeconds;

    public JwtProvider(
            @Value("${oakey.jwt.secret}") String secret,
            @Value("${oakey.jwt.access-exp-seconds}") long accessExpSeconds,
            @Value("${oakey.jwt.refresh-exp-seconds}") long refreshExpSeconds
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpSeconds = accessExpSeconds;
        this.refreshExpSeconds = refreshExpSeconds;
    }

    public String createAccessToken(Long userId) {
        return createToken(userId, accessExpSeconds, "access");
    }

    public String createRefreshToken(Long userId) {
        return createToken(userId, refreshExpSeconds, "refresh");
    }

    private String createToken(Long userId, long expSeconds, String type) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expSeconds);

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("typ", type)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    public Jws<Claims> parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
    }

    public boolean isValid(String token) {
        try {
            parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Long getUserId(String token) {
        Claims claims = parse(token).getPayload();
        return Long.parseLong(claims.getSubject());
    }

    public String getType(String token) {
        Claims claims = parse(token).getPayload();
        Object typ = claims.get("typ");
        return typ == null ? null : String.valueOf(typ);
    }
}
