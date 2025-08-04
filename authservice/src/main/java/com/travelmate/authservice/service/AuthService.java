package com.travelmate.authservice.service;

import com.travelmate.authservice.dto.*;
import com.travelmate.authservice.entity.Role;
import com.travelmate.authservice.entity.User;
import com.travelmate.authservice.repository.RoleRepository;
import com.travelmate.authservice.repository.UserRepository;
import com.travelmate.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            return new AuthResponse(false, "Email already registered", null, null, null);
        }

        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setDob(request.getDob());
        if (request.getGender() != null) {
            user.setGender(User.Gender.valueOf(request.getGender().toUpperCase()));
        }
        user.setEmailVerified(true); // Auto-verify for now

        // Assign default role
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        // Generate tokens
        List<String> roleNames = savedUser.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        String token = jwtUtil.generateToken(
                savedUser.getUserId().toString(),
                savedUser.getName(),
                savedUser.getEmail(),
                roleNames
        );
        String refreshToken = jwtUtil.generateRefreshToken(savedUser.getUserId().toString());

        // Create user info
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                savedUser.getUserId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getProfileImg(),
                roleNames
        );

        return new AuthResponse(true, "User registered successfully", token, refreshToken, userInfo);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        // Generate tokens
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        String token = jwtUtil.generateToken(
                user.getUserId().toString(),
                user.getName(),
                user.getEmail(),
                roleNames
        );
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId().toString());

        // Create user info
        AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getProfileImg(),
                roleNames
        );

        return new AuthResponse(true, "Login successful", token, refreshToken, userInfo);
    }

    public TokenValidationResponse validateToken(String token) {
        try {
            if (!jwtUtil.validateToken(token)) {
                return new TokenValidationResponse(false, null, null, null, null, "Invalid token");
            }

            String userId = jwtUtil.getUserIdFromToken(token);
            String username = jwtUtil.getUsernameFromToken(token);
            String email = jwtUtil.getEmailFromToken(token);
            List<String> roles = jwtUtil.getRolesFromToken(token);

            return new TokenValidationResponse(true, userId, username, email, roles, "Token is valid");
        } catch (Exception e) {
            return new TokenValidationResponse(false, null, null, null, null, "Token validation failed");
        }
    }

    public AuthResponse refreshToken(String refreshToken) {
        try {
            if (!jwtUtil.validateRefreshToken(refreshToken)) {
                return new AuthResponse(false, "Invalid refresh token", null, null, null);
            }

            String userId = jwtUtil.getUserIdFromRefreshToken(refreshToken);
            User user = userRepository.findById(Long.parseLong(userId))
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Generate new tokens
            List<String> roleNames = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());

            String newToken = jwtUtil.generateToken(
                    user.getUserId().toString(),
                    user.getName(),
                    user.getEmail(),
                    roleNames
            );
            String newRefreshToken = jwtUtil.generateRefreshToken(user.getUserId().toString());

            // Create user info
            AuthResponse.UserInfo userInfo = new AuthResponse.UserInfo(
                    user.getUserId(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getProfileImg(),
                    roleNames
            );

            return new AuthResponse(true, "Token refreshed successfully", newToken, newRefreshToken, userInfo);
        } catch (Exception e) {
            return new AuthResponse(false, "Token refresh failed", null, null, null);
        }
    }
}
