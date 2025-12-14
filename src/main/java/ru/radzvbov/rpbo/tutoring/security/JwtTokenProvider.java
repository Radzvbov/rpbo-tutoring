package ru.radzvbov.rpbo.tutoring.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.radzvbov.rpbo.tutoring.domain.User;
import ru.radzvbov.rpbo.tutoring.enums.Role;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-expiration-ms}")
    private long accessExpirationMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // --------------------- Создание токенов ---------------------

    public String generateAccessToken(User user, String sessionId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessExpirationMs);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .addClaims(Map.of(
                        "userId", user.getId(),
                        "role", user.getRole().name(),
                        "sessionId", sessionId
                ))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(User user, String sessionId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshExpirationMs);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .addClaims(Map.of(
                        "type", "refresh",
                        "sessionId", sessionId
                ))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // --------------------- Валидация ---------------------

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;

        } catch (ExpiredJwtException e) {
            return false;

        } catch (JwtException e) {
            return false;
        }
    }

    // --------------------- Извлечения данных ---------------------

    public Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserId(String token) {
        return Long.parseLong(getClaims(token).get("userId").toString());
    }

    public String getSessionId(String token) {
        return getClaims(token).get("sessionId").toString();
    }

    public Role getRole(String token) {
        return Role.valueOf(getClaims(token).get("role").toString());
    }

    public boolean isRefreshToken(String token) {
        Object type = getClaims(token).get("type");
        return type != null && type.equals("refresh");
    }
}