package com.sahil.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Collection;
import java.util.Date;

@Service
@Slf4j
public class JwtService {

    private final SecretKey key;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.key = new SecretKeySpec(Decoders.BASE64URL.decode(secret), "HmacSha256");
    }

    public String grantToken(String username, Collection<? extends GrantedAuthority> authorities) {
        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + 1000 * 60 * 60 * 24 * 10);
        return Jwts
                .builder()
                .subject(username)
                .claim("scope", authorities.stream().map(GrantedAuthority::getAuthority).toList())
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(key)
                .compact();
    }

    public Claims verifyToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts
                    .parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException ex) {
            log.error("Invalid Bearer Token Presented.");
        }
        return claims;
    }
}
