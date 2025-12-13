package com.api.mithai.security;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.api.mithai.auth.entity.User;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccessTokenManager {

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${app.jwt.accessToken.expirationInMs}")
    private long jwtAccessTokenExpirationInMs;

    public String getAccessToken(User user){
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
               userDetails,null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenProvider.generateToken(user, jwtAccessTokenExpirationInMs);
    }

    public Long getUserIdFromJwt(String token) {
        Claims claims = jwtTokenProvider.extractAllClaims(token);
        return Long.parseLong(claims.getSubject());
    }

    public boolean validateJwtToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }
}
