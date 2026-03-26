package com.sahil.security;

import com.sahil.model.Authority;
import com.sahil.model.User;
import com.sahil.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class BearerTokenAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public BearerTokenAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            Claims claims = jwtService.verifyToken(token);

            if (claims != null) {
                Long userId = claims.get("userId", Long.class);
                String username = claims.getSubject();
                List<String> roles = claims.get("roles", ArrayList.class);
                Set<Authority> authorities = roles.stream()
                        .map(Authority::valueOf)
                        .collect(Collectors.toSet());

                User user = User.builder()
                        .id(userId)
                        .username(username)
                        .authorities(authorities)
                        .build();

                UsernamePasswordAuthenticationToken authentication = UsernamePasswordAuthenticationToken
                        .authenticated(user, null, user.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
