package dandastino.chess.auth;

import dandastino.chess.users.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;

@Component
public class AuthTools {
    private static final int HMAC_SHA256_MIN_BYTES = 32;
    private final SecretKey signingKey;

    public AuthTools(@Value("${jwt.secret:}") String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("jwt.secret is not set in configuration (env.properties)");
        }
        this.signingKey = keyFromSecret(secret);
    }

    /** Derives a 32-byte key for HS256 from any-length secret (JJWT requires at least 256 bits). */
    private static SecretKey keyFromSecret(String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < HMAC_SHA256_MIN_BYTES) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                keyBytes = digest.digest(keyBytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException("SHA-256 not available", e);
            }
        } else if (keyBytes.length > HMAC_SHA256_MIN_BYTES) {
            byte[] truncated = new byte[HMAC_SHA256_MIN_BYTES];
            System.arraycopy(keyBytes, 0, truncated, 0, HMAC_SHA256_MIN_BYTES);
            keyBytes = truncated;
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String createToken(User user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("User and user id must not be null");
        }
        return Jwts.builder()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30))
                .subject(user.getId().toString())
                .signWith(signingKey)
                .compact();
    }

    public void validateToken(String token) {
        Jwts.parser().verifyWith(signingKey).build().parse(token);
    }

    public UUID getUserIdFromToken(String token) {
        return UUID.fromString(
                Jwts.parser().verifyWith(signingKey).build()
                        .parseSignedClaims(token)
                        .getPayload().getSubject());
    }
}
