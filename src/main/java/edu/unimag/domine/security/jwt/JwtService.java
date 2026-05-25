package edu.unimag.domine.security.jwt;

import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationSeconds;

    public JwtService(
        @Value("${security.jwt.secret}") String secret,
        @Value("${security.jwt.expiration}") long expirationSeconds
    ){
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(UserDetails principal,Map<String, Object> claims){
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(principal.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .signWith(key)
            .setExpiration(new Date(System.currentTimeMillis() + expirationSeconds * 1000))
            .compact();
    }

    public String extractUsername(String token){
        return parseAllClaims(token).getSubject();
    }
    
    public boolean isTokenValid(String token, UserDetails userDetails){
        try {
            String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    private Claims parseAllClaims(String token){
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    private boolean isTokenExpired(String token){
        return parseAllClaims(token).getExpiration().before(new Date());
    }

    public Long getExpirationSeconds() {
        return expirationSeconds;
    }
    
}
