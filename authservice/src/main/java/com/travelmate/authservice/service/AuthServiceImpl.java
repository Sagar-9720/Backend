package com.travelmate.authservice.service;

import com.travelmate.authservice.dto.*;
import com.travelmate.authservice.entity.Role;
import com.travelmate.authservice.entity.User;
import com.travelmate.authservice.entity.PasswordResetToken;
import com.travelmate.authservice.entity.EmailVerificationToken;
import com.travelmate.authservice.repository.RoleRepository;
import com.travelmate.authservice.repository.UserRepository;
import com.travelmate.authservice.repository.PasswordResetTokenRepository;
import com.travelmate.authservice.repository.EmailVerificationTokenRepository;
import com.travelmate.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final JwtUtil jwtUtil;

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailServiceClient emailServiceClient;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public AuthResponse register(RegisterRequest request) {
        logger.info("Registering user with email: {}", request.getEmail());
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Attempt to register with already registered email: {}", request.getEmail());
            return new AuthResponse(false, "Email already registered", null, null, null);
        }
        // Create new user
        User user = new User().builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .dob(request.getDob())
                .password(passwordEncoder.encode(request.getPassword()))
                .emailVerified(false)  // Set to false initially
                .build();

        if (request.getGender() != null) {
            user.setGender(User.Gender.valueOf(request.getGender().toUpperCase()));
        }

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        user.setRole(userRole);

        User savedUser = userRepository.save(user);
        logger.info("User saved with ID: {}", savedUser.getUserId());

        // Generate tokens immediately after registration
        String token = jwtUtil.generateToken(
                savedUser.getUserId().toString(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getRole().getName()
        );
        String refreshToken = jwtUtil.generateRefreshToken(savedUser.getUserId().toString());

        // Generate and send verification email
        try {
            String emailToken = UUID.randomUUID().toString();
            LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);

            EmailVerificationToken verificationToken = new EmailVerificationToken(emailToken, savedUser, expiryDate);
            emailVerificationTokenRepository.save(verificationToken);

            String verificationLink = frontendUrl + "/verify-email?token=" + emailToken;
            emailServiceClient.sendVerificationEmail(savedUser.getEmail(), savedUser.getName(), verificationLink);
        } catch (Exception e) {
            logger.error("Failed to send verification email to {}: {}", savedUser.getEmail(), e.getMessage(), e);
        }

        // Create user info
        UserInfoDTO userInfo = new UserInfoDTO(
                savedUser.getUserId().toString(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getPhone(),
                savedUser.getDob() != null ? savedUser.getDob().toString() : null,
                savedUser.getGender() != null ? savedUser.getGender().toString() : null,
                savedUser.getProfileImg() != null ? savedUser.getProfileImg() : "",
                savedUser.getRole().getName()
        );
        logger.info("UserInfoDTO created for userId: {}", userInfo);

        return new AuthResponse(true, "User registered successfully. Please check your email to verify your account.", token, refreshToken, userInfo);
    }

    @Override
    public AuthResponse verifyEmail(String token) {
        logger.info("Verifying email with token: {}", token);
        try {
            EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token)
                    .orElseThrow(() -> new RuntimeException("Invalid verification token"));

            if (verificationToken.isExpired()) {
                logger.warn("Verification token expired: {}", token);
                emailVerificationTokenRepository.delete(verificationToken);
                return new AuthResponse(false, "Verification token has expired", null, null, null);
            }

            User user = verificationToken.getUser();
            logger.info("Email verification token valid for userId: {}", user.getUserId());
            user.setEmailVerified(true);
            userRepository.save(user);
            logger.info("User email marked as verified for userId: {}", user.getUserId());

            // Delete the used token
            emailVerificationTokenRepository.delete(verificationToken);
            logger.info("Verification token deleted for userId: {}", user.getUserId());

            // Generate auth tokens
            String authToken = jwtUtil.generateToken(
                    user.getUserId().toString(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().getName()
            );
            String refreshToken = jwtUtil.generateRefreshToken(user.getUserId().toString());
            logger.info("Auth and refresh tokens generated for userId: {}", user.getUserId());

            UserInfoDTO userInfo = new UserInfoDTO(
                    user.getUserId().toString(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getDob() != null ? user.getDob().toString() : null,
                    user.getGender() != null ? user.getGender().toString() : null,
                    user.getProfileImg() != null ? user.getProfileImg() : "",
                    user.getRole().getName()
            );
            logger.info("UserInfoDTO created for userId: {}", user.getUserId());

            return new AuthResponse(true, "Email verified successfully", authToken, refreshToken, userInfo);
        } catch (Exception e) {
            logger.error("Exception in verifyEmail for token: {}", token, e);
            return new AuthResponse(false, "Email verification failed: " + e.getMessage(), null, null, null);
        }
    }

    @Override
    public AuthResponse resendVerificationEmail(String email) {
        logger.info("Resending verification email to: {}", email);
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getEmailVerified()) {
                return new AuthResponse(false, "Email is already verified", null, null, null);
            }

            String token = UUID.randomUUID().toString();
            LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);

            // Delete any existing tokens
            emailVerificationTokenRepository.findByToken(token).ifPresent(emailVerificationTokenRepository::delete);

            EmailVerificationToken verificationToken = new EmailVerificationToken(token, user, expiryDate);
            emailVerificationTokenRepository.save(verificationToken);

            String verificationLink = frontendUrl + "/verify-email?token=" + token;
            emailServiceClient.sendVerificationEmail(user.getEmail(), user.getName(), verificationLink);

            return new AuthResponse(true, "Verification email sent successfully", null, null, null);
        } catch (Exception e) {
            return new AuthResponse(false, "Failed to send verification email: " + e.getMessage(), null, null, null);
        }
    }

    public AuthResponse login(LoginRequest request) {
        logger.info("Login attempt for email: {}", request.getEmail());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        // Generate tokens
        String role = user.getRole().getName();

        String token = jwtUtil.generateToken(
                user.getUserId().toString(),
                user.getName(),
                user.getEmail(),
                role
        );
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId().toString());

        // Create user info
        UserInfoDTO userInfo = new UserInfoDTO(
                user.getUserId().toString(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getDob() != null ? user.getDob().toString() : null,
                user.getGender() != null ? user.getGender().toString() : null,
                user.getProfileImg() != null ? user.getProfileImg() : "",
                role
        );
        logger.info("UserInfoDTO created for userId: {}", user.getUserId());

        return new AuthResponse(true, "Login successful", token, refreshToken, userInfo);
    }

    public TokenValidationResponse validateToken(String token) {
        logger.info("Validating token");
        try {
            if (!jwtUtil.validateToken(token)) {
                return new TokenValidationResponse(false, null, null, null, null, "Invalid token");
            }

            String userId = jwtUtil.getUserIdFromToken(token);
            String name = jwtUtil.getNameFromToken(token);
            String email = jwtUtil.getEmailFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            logger.info("Token created for userId: {}", userId);
            return new TokenValidationResponse(true, userId, name, email, role, "Token is valid");
        } catch (Exception e) {
            return new TokenValidationResponse(false, null, null, null, null, "Token validation failed");
        }
    }

    public AuthResponse refreshToken(String refreshToken) {
        logger.info("Refreshing token");
        try {
            if (!jwtUtil.validateRefreshToken(refreshToken)) {
                return new AuthResponse(false, "Invalid refresh token", null, null, null);
            }

            String userId = jwtUtil.getUserIdFromRefreshToken(refreshToken);
            User user = userRepository.findById(Long.parseLong(userId))
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Generate new tokens
            String role = user.getRole().getName();

            String newToken = jwtUtil.generateToken(
                    user.getUserId().toString(),
                    user.getName(),
                    user.getEmail(),
                    role
            );
            String newRefreshToken = jwtUtil.generateRefreshToken(user.getUserId().toString());
            logger.info("New refresh token created for userId: {}", user.getUserId());
            // Create user info
            UserInfoDTO userInfo = new UserInfoDTO(
                    user.getUserId().toString(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getDob() != null ? user.getDob().toString() : null,
                    user.getGender() != null ? user.getGender().toString() : null,
                    user.getProfileImg() != null ? user.getProfileImg() : "",
                    role
            );

            return new AuthResponse(true, "Token refreshed successfully", newToken, newRefreshToken, userInfo);
        } catch (Exception e) {
            return new AuthResponse(false, "Token refresh failed", null, null, null);
        }
    }

    @Override
    public LogoutResponse logout(String token) {
        logger.info("Logout attempt");
        if (token != null && !token.isEmpty()) {
            String name = jwtUtil.getNameFromToken(token);
            jwtUtil.revokeToken(token);
            return new LogoutResponse(name, "Successfully logged out");
        }
        return new LogoutResponse((String) null, "Invalid token");
    }

    @Override
    public UserInfoDTO updateUserInfo(UserUpdateInfoRequest request) {
        logger.info("Updating user info for userId: {}", request.getUserId());
        User user = userRepository.findById(Long.parseLong(request.getUserId()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getName() != null) user.setName(request.getName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getDob() != null) user.setDob(LocalDate.parse(request.getDob()));
        if (request.getProfileImg() != null) user.setProfileImg(request.getProfileImg());

        User updatedUser = userRepository.save(user);
        logger.info("User info updated for userId: {}", updatedUser.getUserId());
        logger.info("Updated user info: {}", updatedUser);
        return new UserInfoDTO(
                updatedUser.getUserId().toString(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getPhone(),
                updatedUser.getDob() != null ? updatedUser.getDob().toString() : null,
                updatedUser.getGender() != null ? updatedUser.getGender().toString() : null,
                updatedUser.getProfileImg() != null ? updatedUser.getProfileImg() : "",
                updatedUser.getRole().getName()
        );
    }

    @Override
    public UserInfoDTO deleteUser(String token, String userId) {
        logger.info("Deleting user with userId: {}", userId);
        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Invalid token");
        }

        String role = jwtUtil.getRoleFromToken(token);
        if (!role.equals("ADMIN")) {
            throw new RuntimeException("Unauthorized access");
        }

        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Store user info before deletion for return
        UserInfoDTO userInfo = new UserInfoDTO(
                user.getUserId().toString(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getDob() != null ? user.getDob().toString() : null,
                user.getGender() != null ? user.getGender().toString() : null,
                user.getProfileImg() != null ? user.getProfileImg() : "",
                user.getRole().getName()
        );

        try {
            userRepository.delete(user);
            return userInfo;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user: " + e.getMessage());
        }

    }


    @Override
    public UserInfoDTO changePassword(UserUpdateInfoRequest request) {
        logger.info("Changing password for userId: {}", request.getUserId());
        User user = userRepository.findById(Long.parseLong(request.getUserId()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid old password");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        User updatedUser = userRepository.save(user);

        return new UserInfoDTO(
                updatedUser.getUserId().toString(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getPhone(),
                updatedUser.getDob() != null ? updatedUser.getDob().toString() : null,
                updatedUser.getGender() != null ? updatedUser.getGender().toString() : null,
                updatedUser.getProfileImg() != null ? updatedUser.getProfileImg() : "",
                updatedUser.getRole().getName()
        );
    }

    @Override
    public AuthResponse initiatePasswordReset(String email) {
        logger.info("Initiating password reset for email: {}", email);
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String token = UUID.randomUUID().toString();
            LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(30);

            PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
            passwordResetTokenRepository.save(resetToken);

            String resetLink = frontendUrl + "/reset-password?token=" + token;
            emailServiceClient.sendPasswordResetEmail(user.getEmail(), user.getName(), resetLink);

            return new AuthResponse(true, "Password reset email sent", null, null, null);
        } catch (Exception e) {
            return new AuthResponse(false, "Failed to send password reset email", null, null, null);
        }
    }

    @Override
    public AuthResponse resetPassword(UserUpdateInfoRequest request) {
        logger.info("Resetting password for userId: {}", request.getUserId());
        try {
            PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken())
                    .orElseThrow(() -> new RuntimeException("Invalid token"));

            if (resetToken.isExpired()) {
                passwordResetTokenRepository.delete(resetToken);
                return new AuthResponse(false, "Reset has expired", null, null, null);
            }

            User user = resetToken.getUser();
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            userRepository.save(user);

            // Delete the used token
            passwordResetTokenRepository.delete(resetToken);

            // Generate new auth tokens
            String token = jwtUtil.generateToken(
                    user.getUserId().toString(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().getName()
            );
            String refreshToken = jwtUtil.generateRefreshToken(user.getUserId().toString());

            UserInfoDTO userInfo = new UserInfoDTO(
                    user.getUserId().toString(),
                    user.getName(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getDob() != null ? user.getDob().toString() : null,
                    user.getGender() != null ? user.getGender().toString() : null,
                    user.getProfileImg() != null ? user.getProfileImg() : "",
                    user.getRole().getName()
            );

            return new AuthResponse(true, "Password reset successful", token, refreshToken, userInfo);
        } catch (Exception e) {
            return new AuthResponse(false, "Password reset failed: " + e.getMessage(), null, null, null);
        }
    }

    @Override
    public UserInfoDTO getUserInfo(String token) {
        logger.info("Fetching user info");
        String userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        return new UserInfoDTO(
                user.getUserId().toString(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getDob() != null ? user.getDob().toString() : null,
                user.getGender() != null ? user.getGender().toString() : null,
                user.getProfileImg() != null ? user.getProfileImg() : "",
                user.getRole().getName()
        );
    }

    @Override
    public List<UserInfoDTO> getAllUsers(String token) {
        logger.info("Fetching all users");
        String role = jwtUtil.getRoleFromToken(token);
        if (!role.equals("ADMIN")) {
            throw new RuntimeException("Unauthorized access");
        }

        return userRepository.findAll().stream()
                .map(user -> new UserInfoDTO(
                        user.getUserId().toString(),
                        user.getName(),
                        user.getEmail(),
                        user.getPhone(),
                        user.getDob() != null ? user.getDob().toString() : null,
                        user.getGender() != null ? user.getGender().toString() : null,
                        user.getProfileImg() != null ? user.getProfileImg() : "",
                        user.getRole().getName()
                )).toList();
    }

    @Override
    public UserInfoDTO updateRoleToUser(String role,UserUpdateInfoRequest request) {
        logger.info("Updating role to {} for userId: {}", role, request.getUserId());
        User user = userRepository.findById(Long.parseLong(request.getUserId()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        Role newRole = roleRepository.findByName(role.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Role not found"));

        user.setRole(newRole);
        User updatedUser = userRepository.save(user);

        return new UserInfoDTO(
                updatedUser.getUserId().toString(),
                updatedUser.getName(),
                updatedUser.getEmail(),
                updatedUser.getPhone(),
                updatedUser.getDob() != null ? updatedUser.getDob().toString() : null,
                updatedUser.getGender() != null ? updatedUser.getGender().toString() : null,
                updatedUser.getProfileImg() != null ? updatedUser.getProfileImg() : "",
                updatedUser.getRole().getName()
        );
    }

    @Override
    public UserInfoDTO checkEmailExists(String email) {
        logger.info("Checking if email exists: {}", email);
        User user = userRepository.findByEmail(email)
                .orElse(null);

        if (user == null) {
            return null;
        }

        return new UserInfoDTO(
                user.getUserId().toString(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getDob() != null ? user.getDob().toString() : null,
                user.getGender() != null ? user.getGender().toString() : null,
                user.getProfileImg() != null ? user.getProfileImg() : "",
                user.getRole().getName()
        );
    }

    @Override
    public List<Role> getAllRoles() {
        logger.info("Fetching all roles");
        return roleRepository.findAll();
    }

}
