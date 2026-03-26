package com.sahil.service;

import com.sahil.model.Authority;
import com.sahil.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@Slf4j
public class  JwtService {

    private final SecretKey key;

    public JwtService(@Value("${jwt.secret}") String secret) {
        this.key = new SecretKeySpec(Decoders.BASE64URL.decode(secret), "HmacSha256");
    }

    public String grantToken(User user) {
        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + 1000 * 60 * 60 * 24 * 10);
        return Jwts
                .builder()
                .subject(user.getUsername())
                .claim("userId", user.getId())
                .claim("roles", user.getAuthorities().stream().map(grantedAuthority -> Authority.valueOf(grantedAuthority.getAuthority())).collect(Collectors.toSet()))
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
