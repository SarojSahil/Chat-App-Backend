package com.sahil.chatapp.security;

import com.sahil.chatapp.model.SystemRole;
import com.sahil.chatapp.model.User;
import com.sahil.chatapp.service.JwtService;
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
                Long userId = Long.parseLong(claims.getSubject());
                SystemRole role = SystemRole.valueOf(claims.get("role", String.class));
                String name = claims.get("name", String.class);
                String phoneNumber = claims.get("phoneNumber", String.class);

                User user = User.builder()
                        .id(userId)
                        .name(name)
                        .phoneNumber(phoneNumber)
                        .role(role)
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
