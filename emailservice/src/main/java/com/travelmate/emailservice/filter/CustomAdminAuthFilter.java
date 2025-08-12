package com.travelmate.emailservice.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Component
class AuthServiceFeignClientFallback implements AuthServiceFeignClient {
    @Override
    public Map<String, Object> login(Map<String, String> request) {
        return Map.of(); // Return empty map or error info
    }

    @Override
    public Map<String, Object> validateToken(String token) {
        return Map.of(); // Return empty map or error info
    }
}

@FeignClient(name = "auth-service", fallback = AuthServiceFeignClientFallback.class)
interface AuthServiceFeignClient {
    @PostMapping("/api/auth/login")
    Map<String, Object> login(@RequestBody Map<String, String> request);

    @PostMapping("/api/auth/validate")
    Map<String, Object> validateToken(@RequestHeader("Authorization") String token);
}

@Component
public class CustomAdminAuthFilter extends OncePerRequestFilter {
    @Autowired
    private AuthServiceFeignClient authServiceFeignClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") || path.startsWith("/swagger-resources") || path.startsWith("/webjars")) {
            String email = request.getParameter("username");
            String password = request.getParameter("password");
            if (email == null || password == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Email and password required");
                return;
            }
            Map<String, Object> loginResponse = authServiceFeignClient.login(Map.of("email", email, "password", password));
            String token = (String) loginResponse.get("token");
            if (token == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid credentials");
                return;
            }
            Map<String, Object> validateResponse = authServiceFeignClient.validateToken("Bearer " + token);
            String role = (String) validateResponse.get("role");
            if (!"ADMIN".equals(role) && !"SUBADMIN".equals(role)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Admin or Subadmin role required");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
