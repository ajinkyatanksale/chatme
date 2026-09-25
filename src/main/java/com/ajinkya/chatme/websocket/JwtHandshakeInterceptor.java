package com.ajinkya.chatme.websocket;

import com.ajinkya.chatme.entity.User;
import com.ajinkya.chatme.security.JwtService;
import com.ajinkya.chatme.security.CustomUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    JwtService jwtService;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String queryString = request.getURI().getQuery();
        if (null == queryString || queryString.isBlank()) {
            return false;
        }

        Map<String, String> queryParams = Arrays.stream(queryString.split("&")).map(s -> s.split("=")).collect(Collectors.toMap(strings -> strings[0], strings -> strings[1]));
        String token = Objects.requireNonNull(queryParams.get("token"));
        String username = jwtService.extractUsername(token);
        User userDetails = customUserDetailsService.loadUserByUsername(username);
        if (!jwtService.isTokenValid(token, userDetails)) {
            return false;
        } else {
            attributes.put("username", username);
            return true;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @Nullable Exception exception) {

    }
}
