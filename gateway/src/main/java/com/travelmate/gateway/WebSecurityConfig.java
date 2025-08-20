package com.travelmate.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.csrf(csrf -> csrf.disable()).authorizeExchange(exchanges -> exchanges.anyExchange().permitAll()).httpBasic(withDefaults()).build();

    }
}