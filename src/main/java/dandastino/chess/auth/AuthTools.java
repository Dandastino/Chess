package dandastino.chess.auth;

import dandastino.chess.users.User;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;

import java.util.Date;

@Component
public class AuthTools {
    private final String secret;

    public AuthTools(@Value("${jwt.secret}") String secret){
        this.secret = secret;
    }

    public String createToken(User user){
        return Jwts.builder()
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30))
                .subject(user.getId().toString())
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    public void validateToken(String token){
        Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes())).build().parse(token);
    }
}
