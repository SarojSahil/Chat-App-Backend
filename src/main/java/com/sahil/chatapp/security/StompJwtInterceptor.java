package com.sahil.chatapp.security;

import com.sahil.chatapp.model.StompPrincipal;
import com.sahil.chatapp.service.JwtService;
import io.jsonwebtoken.Claims;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class StompJwtInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;

    public StompJwtInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String header = accessor.getFirstNativeHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);
                Claims claims = jwtService.verifyToken(token);

                if (claims == null) {
                    throw new MessageDeliveryException("Invalid Token Presented.");
                }
                Long userId = Long.parseLong(claims.getSubject());
                String name = claims.get("name", String.class);
                String phoneNumber = claims.get("phoneNumber", String.class);

                StompPrincipal principal = StompPrincipal
                        .builder()
                        .userId(userId)
                        .username(name)
                        .phoneNumber(phoneNumber)
                        .build();

                accessor.setUser(principal);
            }
        }
        return message;
    }
}
