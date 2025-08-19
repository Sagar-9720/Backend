package com.travelmate.gateway.filter;

import com.travelmate.gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class CustomAuthFilter implements GlobalFilter, Ordered {

    private final WebClient webClient;

    // Simple in-memory cache for tokens
    private final Map<String, Claims> tokenCache = new ConcurrentHashMap<>();

    @Autowired
    private JwtUtil jwtUtil;

    public CustomAuthFilter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        // Allow auth endpoints without validation
        if (path.startsWith("/api/auth")) {
            return chain.filter(exchange);
        }
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        String token = authHeader.substring(7);
        Claims claims = tokenCache.get(token);
        if (claims == null) {
            if (!jwtUtil.validateToken(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            claims = jwtUtil.getClaims(token);
            tokenCache.put(token, claims);
        }
        // Pass user info in headers
        String userInfoJson = String.format(
                "{\"userId\":\"%s\",\"username\":\"%s\",\"role\":\"%s\",\"email\":\"%s\"}",
                jwtUtil.getUserIdFromToken(token),
                jwtUtil.getNameFromToken(token),
                jwtUtil.getRoleFromToken(token),
                jwtUtil.getEmailFromToken(token)
        );
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("X-User-Info", userInfoJson)
                .build();
        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();
        return chain.filter(mutatedExchange);
    }

    @Override
    public int getOrder() {
        return -1; // High precedence
    }
}
