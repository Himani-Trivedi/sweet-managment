package com.api.mithai.security;

import com.api.mithai.auth.entity.User;
import com.api.mithai.base.exception.ResponseStatusException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class JwtTokenProvider {

    @Value("${app.jwtSecret}")
    private String jwtSecrete;

    String generateToken(User user, Long jwtAccessTokenExpirationInMs) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtAccessTokenExpirationInMs);

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = jwtSecrete.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    boolean validateToken(String jwt) {
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(jwt);
            return true;
        } catch (Exception ex) {
            throw new BadCredentialsException("error.user.authentication", ex);
        }
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSigningKey()).build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception ex) {
            log.error("Exception - JWT claims string is empty.");
            throw new ResponseStatusException("JWT claims string is empty.", HttpStatus.UNAUTHORIZED);
        }
    }
}

