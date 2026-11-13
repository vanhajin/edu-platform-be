package org.store.authsvc.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;

@Service
public class TokenService {

    private static final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final SecretKey signingKey;
    private final long accessTokenExpirationSeconds;
    private final long refreshTokenExpirationDays;

    public TokenService(
            @Value("${auth.jwt.secret}") String jwtSecret,
            @Value("${auth.jwt.access-token-expiration-seconds:900}") long accessTokenExpirationSeconds,
            @Value("${auth.jwt.refresh-token-expiration-days:30}") long refreshTokenExpirationDays
    ) {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException("JWT_SECRET must be configured");
        }
        this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
        this.accessTokenExpirationSeconds = accessTokenExpirationSeconds;
        this.refreshTokenExpirationDays = refreshTokenExpirationDays;
    }

    public String generateAccessToken(String userId, String username, String role) {
        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + accessTokenExpirationSeconds * 1000);
        return Jwts.builder()
                .subject(userId)
                .claim("username", username)
                .claim("role", role)
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(signingKey)
                .compact();
    }

    public String generateRefreshToken() {
        byte[] bytes = new byte[64];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public String hashRefreshToken(String refreshToken) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(refreshToken.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    public LocalDateTime refreshTokenExpiresAt() {
        return currentTime().plusDays(refreshTokenExpirationDays);
    }

    public LocalDateTime currentTime() {
        return LocalDateTime.now(VIETNAM_ZONE);
    }

    public long accessTokenExpirationSeconds() {
        return accessTokenExpirationSeconds;
    }
}
