package authentication;

import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.security.Key;

public class JwtService {

    private static final String API_KEY = "meowmeowmeow";

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(API_KEY.getBytes(StandardCharsets.UTF_8));
    }
}

