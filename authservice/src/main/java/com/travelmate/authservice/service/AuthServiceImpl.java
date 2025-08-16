package com.travelmate.authservice.service;

import com.travelmate.authservice.dto.*;
import com.travelmate.authservice.entity.User;
import com.travelmate.authservice.entity.PasswordResetToken;
import com.travelmate.authservice.entity.EmailVerificationToken;
import com.travelmate.authservice.mapper.UserMapper;
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
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;

import com.travelmate.authservice.exception.EmailNotFoundException;
import com.travelmate.authservice.exception.UserNotFoundException;
import com.travelmate.authservice.exception.UnauthorizedAccessException;
import com.travelmate.authservice.exception.EmailAlreadyExistException;

import static java.util.stream.Collectors.toList;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private final UserRepository userRepository;


    private final PasswordEncoder passwordEncoder;
    @Autowired
    private final JwtUtil jwtUtil;

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailServiceClient emailServiceClient;

    @Value("${app.frontend.url}")

    private String frontendUrl;

    public AuthResponse register(RegisterRequest request) {
        logger.info("Registering user with email: {}", request.email());
        if (userRepository.existsByEmail(request.email())) {
            logger.warn("Attempt to register with already registered email: {}", request.email());
            throw new EmailAlreadyExistException("Email already exists: " + request.email());
        }
        // Create new user
        User user = User.builder().name(request.name()).email(request.email()).phone(request.phone()).dob(request.dob()).password(passwordEncoder.encode(request.password())).build();

        if (request.gender() != null) {
            user.setGender(User.Gender.valueOf(request.gender().toUpperCase()));
        }

        user.setRole(User.Role.valueOf("USER"));

        User savedUser = userRepository.save(user);
        logger.info("User saved with ID: {}", savedUser.getUserId());

        // Generate tokens immediately after registration
        String token = jwtUtil.generateToken(savedUser.getUserId().toString(), savedUser.getName(), savedUser.getEmail(), savedUser.getRole().toString());
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
        UserInfoDTO userInfo = UserMapper.toUserInfoDTO(savedUser);

        logger.info("UserInfoDTO created for userId: {}", userInfo);

        return new AuthResponse(true, "User registered successfully. Please check your email to verify your account.", token, refreshToken, userInfo);
    }

    @Override
    public AuthResponse verifyEmail(String token) {
        logger.info("Verifying email with token: {}", token);
        try {
            EmailVerificationToken verificationToken = emailVerificationTokenRepository.findByToken(token).orElseThrow(() -> new UnauthorizedAccessException("Invalid verification token"));

            if (verificationToken.isExpired()) {
                logger.warn("Verification token expired: {}", token);
                emailVerificationTokenRepository.delete(verificationToken);
                throw new UnauthorizedAccessException("Verification token has expired");
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
            String authToken = jwtUtil.generateToken(user.getUserId().toString(), user.getName(), user.getEmail(), user.getRole().toString());
            String refreshToken = jwtUtil.generateRefreshToken(user.getUserId().toString());
            logger.info("Auth and refresh tokens generated for userId: {}", user.getUserId());

            UserInfoDTO userInfo = UserMapper.toUserInfoDTO(user);
            logger.info("UserInfoDTO created for userId: {}", user.getUserId());

            return new AuthResponse(true, "Email verified successfully", authToken, refreshToken, userInfo);
        } catch (UnauthorizedAccessException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Exception in verifyEmail for token: {}", token, e);
            throw new UnauthorizedAccessException("Email verification failed: " + e.getMessage());
        }
    }

    @Override
    public AuthResponse resendVerificationEmail(String token) {
        logger.info("Resending verification email to: {}");
        try {
            if (token == null || token.isEmpty()) {
                throw new UnauthorizedAccessException("Invalid or missing token");
            }
            String email;
            if (jwtUtil.validateToken(token)) {
                email = jwtUtil.getEmailFromToken(token);
                logger.info("Resending verification email for user with email: {}", email);
            } else {
                throw new UnauthorizedAccessException("Invalid token");
            }
            User user = userRepository.findByEmail(email).orElseThrow(() -> new EmailNotFoundException("User not found"));

            if (user.getEmailVerified()) {
                throw new EmailAlreadyExistException("Email is already verified");
            }

            String resetToken = UUID.randomUUID().toString();
            LocalDateTime expiryDate = LocalDateTime.now().plusHours(24);

            EmailVerificationToken verificationToken = new EmailVerificationToken(resetToken, user, expiryDate);
            emailVerificationTokenRepository.save(verificationToken);

            String verificationLink = frontendUrl + "/verify-email?token=" + token;
            emailServiceClient.sendVerificationEmail(user.getEmail(), user.getName(), verificationLink);

            return new AuthResponse(true, "Verification email sent successfully", null, null, null);
        } catch (EmailNotFoundException | EmailAlreadyExistException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to send verification email: " + e.getMessage());
        }
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        logger.info("Login attempt for email: {}", request.email());
        User user = userRepository.findByEmail(request.email()).orElseThrow(() -> new EmailNotFoundException("Email not found: " + request.email()));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedAccessException("Invalid credentials");
        }
        // Generate tokens
        String role = user.getRole().toString();

        String token = jwtUtil.generateToken(user.getUserId().toString(), user.getName(), user.getEmail(), role);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId().toString());


        UserInfoDTO userInfo = UserMapper.toUserInfoDTO(user);
        logger.info("UserInfoDTO created for userId: {}", user.getUserId());

        return new AuthResponse(true, "Login successful", token, refreshToken, userInfo);
    }

    @Override
    public TokenValidationResponse validateToken(String token) throws UnauthorizedAccessException {
        logger.info("Validating token");
        try {
            if (!jwtUtil.validateToken(token)) {
                throw new UnauthorizedAccessException("Invalid token");
            }

            String userId = jwtUtil.getUserIdFromToken(token);
            String name = jwtUtil.getNameFromToken(token);
            String email = jwtUtil.getEmailFromToken(token);
            String role = jwtUtil.getRoleFromToken(token);
            logger.info("Token created for userId: {}", userId);
            return new TokenValidationResponse(true, userId, name, email, role, "Token is valid");
        } catch (UnauthorizedAccessException e) {
            throw e;
        } catch (Exception e) {
            throw new UnauthorizedAccessException("Token validation failed");
        }
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) throws UnauthorizedAccessException {
        logger.info("Refreshing token");
        try {
            if (!jwtUtil.validateRefreshToken(refreshToken)) {
                throw new UnauthorizedAccessException("Invalid refresh token");
            }

            String userId = jwtUtil.getUserIdFromRefreshToken(refreshToken);
            User user = userRepository.findById(Long.parseLong(userId)).orElseThrow(() -> new UserNotFoundException("User not found"));

            // Generate new tokens
            String role = user.getRole().toString();

            String newToken = jwtUtil.generateToken(user.getUserId().toString(), user.getName(), user.getEmail(), role);
            String newRefreshToken = jwtUtil.generateRefreshToken(user.getUserId().toString());
            logger.info("New refresh token created for userId: {}", user.getUserId());
            // Create user info

            UserInfoDTO userInfo = UserMapper.toUserInfoDTO(user);

            return new AuthResponse(true, "Token refreshed successfully", newToken, newRefreshToken, userInfo);
        } catch (UnauthorizedAccessException | UserNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new UnauthorizedAccessException("Token refresh failed");
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
    public UserInfoDTO updateUserInfo(String token, UserUpdateInfoRequest request) throws UserNotFoundException, UnauthorizedAccessException {
        if (!jwtUtil.validateToken(token)) {
            logger.warn("Invalid token for updateUserInfo");
            throw new UnauthorizedAccessException("Invalid token");
        }

        logger.info("Updating user info for userId: {}", request.userId());
        User user = userRepository.findById(Long.parseLong(request.userId())).orElseThrow(() -> new UserNotFoundException("User not found with id: " + request.userId()));

        if (request.name() != null) user.setName(request.name());
        if (request.phone() != null) user.setPhone(request.phone());
        if (request.dob() != null) user.setDob(LocalDate.parse(request.dob()));
        if (request.profileImg() != null) user.setProfileImg(request.profileImg());

        User updatedUser = userRepository.save(user);
        logger.info("User info updated for userId: {}", updatedUser.getUserId());
        logger.info("Updated user info: {}", updatedUser);
        return UserMapper.toUserInfoDTO(updatedUser);
    }

    @Override
    public UserInfoDTO deleteRequest(String token) throws UserNotFoundException, UnauthorizedAccessException {
        logger.info("Delete request initiated for token: {}", token);
        if (token == null || token.isEmpty()) {
            logger.warn("Invalid token for deleteRequest");
            throw new UnauthorizedAccessException("Invalid token");
        }
        String userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(Long.parseLong(userId)).orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        user.setRequestDelete(true);
        User updatedUser = userRepository.save(user);
        // Store user info before deletion for return
        UserInfoDTO userInfo = UserMapper.toUserInfoDTO(updatedUser);
        logger.info("User info stored for deletion: {}", userInfo);
        return userInfo;
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

        User user = userRepository.findById(Long.parseLong(userId)).orElseThrow(() -> new RuntimeException("User not found"));

        // Store user info before deletion for return
        UserInfoDTO userInfo = new UserInfoDTO(user.getUserId().toString(), user.getName(), user.getEmail(), user.getPhone(), user.getDob() != null ? user.getDob().toString() : null, user.getGender() != null ? user.getGender().toString() : null, user.getProfileImg() != null ? user.getProfileImg() : "", user.getRole().toString());

        try {
            userRepository.delete(user);
            return userInfo;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete user: " + e.getMessage());
        }

    }

    @Override
    public UserInfoDTO changePassword(String token, UserUpdateInfoRequest request) {
        if (token == null || token.isEmpty()) {
            logger.warn("Invalid token for changePassword");
            throw new UnauthorizedAccessException("Invalid token");
        }
        if (!jwtUtil.validateToken(token)) {
            logger.warn("Invalid token for changePassword");
            throw new UnauthorizedAccessException("Invalid token");
        }
        logger.info("Changing password for userId: {}", request.userId());
        User user = userRepository.findById(Long.parseLong(request.userId())).orElseThrow(() -> new RuntimeException("User not found"));

        // Verify old password
        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid old password");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(request.password()));
        User updatedUser = userRepository.save(user);

        return UserMapper.toUserInfoDTO(updatedUser);
    }

    @Override
    public AuthResponse resetPasswordRequest(String email) {
        logger.info("Initiating password reset for email: {}", email);
        try {
            User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
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
    public AuthResponse resetPassword(String token, String newPassword) {
        logger.info("Resetting password using token");
        try {
            PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Invalid or expired token"));
            if (resetToken.isExpired()) {
                passwordResetTokenRepository.delete(resetToken);
                return new AuthResponse(false, "Reset token has expired", null, null, null);
            }
            User user = resetToken.getUser();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            // Delete the used token
            passwordResetTokenRepository.delete(resetToken);

            // Generate new auth tokens
            String newtoken = jwtUtil.generateToken(user.getUserId().toString(), user.getName(), user.getEmail(), user.getRole().toString());
            String newrefreshToken = jwtUtil.generateRefreshToken(user.getUserId().toString());

            UserInfoDTO userInfo = UserMapper.toUserInfoDTO(user);

            return new AuthResponse(true, "Password reset successful", newtoken, newrefreshToken, userInfo);
        } catch (Exception e) {
            return new AuthResponse(false, "Password reset failed: " + e.getMessage(), null, null, null);
        }
    }

    @Override
    public UserInfoDTO updateRoleToUser(String token, UserUpdateInfoRequest request) {
        logger.info("Updating role to ADMIN for userId: {}", request.userId());
        if (token == null || !jwtUtil.validateToken(token)) {
            logger.warn("Invalid or missing token for updateRoleToUser");
            throw new UnauthorizedAccessException("Invalid or missing token");
        }
        String userRole = jwtUtil.getRoleFromToken(token);
        if (!"ADMIN".equalsIgnoreCase(userRole)) {
            logger.warn("Unauthorized access by user role: {}", userRole);
            throw new UnauthorizedAccessException("Only ADMIN or SUBADMIN can update user roles");
        }
        User user = userRepository.findById(Long.parseLong(request.userId())).orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(User.Role.valueOf("ADMIN"));
        User updatedUser = userRepository.save(user);
        return UserMapper.toUserInfoDTO(updatedUser);
    }

    @Override
    public UserInfoDTO getUserInfo(String token) {
        logger.info("Fetching user info");
        String userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(Long.parseLong(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        return UserMapper.toUserInfoDTO(user);
    }

    @Override
    public List<UserInfoDTO> getAllUsers(String token) {
        logger.info("Fetching all users");
        String role = jwtUtil.getRoleFromToken(token);
        if (!role.equals("ADMIN")) {
            throw new RuntimeException("Unauthorized access");
        }
        return userRepository.findAll().stream().map(UserMapper::toUserInfoDTO).collect(toList());
    }


    @Override
    public UserInfoDTO checkEmailExists(String email) {
        logger.info("Checking if email exists: {}", email);
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            logger.warn("Email not found: {}", email);
            throw new EmailNotFoundException("Email not found: " + email);
        }

        logger.info("Email found: {}", email);
        return UserMapper.toUserInfoDTO(user);
    }


    @Override
    public AuthResponse registerSubAdmin(RegisterRequest request, String token) throws UnauthorizedAccessException {
        String userRole = jwtUtil.getRoleFromToken(token);
        if (!"ADMIN".equalsIgnoreCase(userRole)) {
            throw new UnauthorizedAccessException("Only ADMIN can register a SUBADMIN");
        }
        logger.info("Registering subadmin with email: {} by {}", request.email(), userRole);
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistException("Email already exists: " + request.email());
        }
        String subAdminRole = "SUBADMIN";
        User user = new User().builder().name(request.name()).email(request.email()).phone(request.phone()).dob(request.dob()).password(passwordEncoder.encode(request.password())).emailVerified(true).role(User.Role.valueOf(subAdminRole)).build();
        User savedUser = userRepository.save(user);
        logger.info("Subadmin saved with ID: {}", savedUser.getUserId());

        UserInfoDTO userInfo = UserMapper.toUserInfoDTO(savedUser);
        return new AuthResponse(true, "Subadmin registered successfully", null, null, userInfo);
    }

    public List<Map<String, String>> getUserNameThroughId(String token, List<String> userIds) {
        logger.info("Fetching user names for userIds: {}", userIds);
        if (token == null || !jwtUtil.validateToken(token)) {
            logger.warn("Invalid token for getUserNameThroughId");
            throw new UnauthorizedAccessException("Invalid token");
        }
        return userRepository.findAllById(userIds.stream().map(Long::parseLong).collect(toList())).stream().map(user -> Map.of("userId", user.getUserId().toString(), "name", user.getName())).collect(toList());
    }
}
