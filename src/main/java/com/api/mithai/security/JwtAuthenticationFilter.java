package com.api.mithai.security;

import com.api.mithai.auth.entity.User;
import com.api.mithai.auth.repository.UserRepository;
import com.api.mithai.base.service.BaseService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import com.api.mithai.base.constants.Urls;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private AccessTokenManager accessTokenManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BaseService baseService;

    @Value("${app.jwtHeaderString}")
    private String accessTokenHeader;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = getAccessTokenFromRequest(request);
        if (!Arrays.stream(baseService.publicEndpoints)
                .anyMatch(publicEndpoint -> request.getRequestURI().startsWith(publicEndpoint))) {
            if (StringUtils.hasText(accessToken) && accessTokenManager.validateJwtToken(accessToken)) {
                Long userId = accessTokenManager.getUserIdFromJwt(accessToken);
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new BadCredentialsException("error.user.authentication"));

                UserDetailsImpl userDetails = new UserDetailsImpl(user);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest httpServletRequest) {
        String requestTokenHeader = httpServletRequest.getHeader(accessTokenHeader);
        if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer")) {
            return "";
        }
        return requestTokenHeader.split("Bearer ")[1];
    }

    private String getAccessTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (Urls.ACCESS_TOKEN.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // Fallback to header if cookie not found
        return getJwtFromRequest(request);
    }


}
