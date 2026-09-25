package com.ajinkya.chatme.security;

import com.ajinkya.chatme.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class JwtService {

    @Value("${app.jwt.expiration-ms}")
    private long JWT_TOKEN_VALIDITY;
    @Value("${app.jwt.secret}")
    private String SECRET;


    public String generateToken(String username) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(key).compact();
    }

    public String extractUsername(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }

    public boolean isTokenValid(String token, User user) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getExpiration().after(new Date()) && extractUsername(token).equals(user.getUsername());
    }

    private Claims getAllClaimsFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }
}
