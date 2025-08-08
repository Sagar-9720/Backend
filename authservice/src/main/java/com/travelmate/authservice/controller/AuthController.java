package com.travelmate.authservice.controller;

import com.travelmate.authservice.dto.*;
import com.travelmate.authservice.entity.Role;
import com.travelmate.authservice.response.CustomResponseEntity;
import com.travelmate.authservice.service.AuthServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private final AuthServiceImpl authServiceImpl;

    @PostMapping("/register")
    public ResponseEntity<CustomResponseEntity<AuthResponse>> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest servletRequest) {
        try {
            AuthResponse response = authServiceImpl.register(request);
            if (response.isSuccess()) {
                return ResponseEntity.status(HttpStatus.CREATED).body(
                    CustomResponseEntity.success(HttpStatus.CREATED.value(), response.getMessage(), response, servletRequest.getRequestURI())
                );
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    CustomResponseEntity.error(HttpStatus.BAD_REQUEST.value(), response.getMessage(), servletRequest.getRequestURI())
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                CustomResponseEntity.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Registration failed: " + e.getMessage(), servletRequest.getRequestURI())
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<CustomResponseEntity<AuthResponse>> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        try {
            AuthResponse response = authServiceImpl.login(request);
            return ResponseEntity.ok(
                CustomResponseEntity.success(HttpStatus.OK.value(), response.getMessage(), response, servletRequest.getRequestURI())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), "Login failed: " + e.getMessage(), servletRequest.getRequestURI())
            );
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<CustomResponseEntity<TokenValidationResponse>> validateToken(@RequestHeader("Authorization") String authHeader, HttpServletRequest servletRequest) {
        try {
            logger.info("Validating token from request: {}", servletRequest.getRequestURI());
            logger.debug("Authorization header: {}", authHeader);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                TokenValidationResponse response = new TokenValidationResponse(false, null, null, null, null, "Invalid authorization header");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), response.getMessage(), servletRequest.getRequestURI())
                );
            }
            String token = authHeader.substring(7);
            TokenValidationResponse response = authServiceImpl.validateToken(token);
            if (response.isValid()) {
                return ResponseEntity.ok(
                    CustomResponseEntity.success(HttpStatus.OK.value(), response.getMessage(), response, servletRequest.getRequestURI())
                );
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), response.getMessage(), servletRequest.getRequestURI())
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                CustomResponseEntity.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Token validation failed", servletRequest.getRequestURI())
            );
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<CustomResponseEntity<AuthResponse>> refreshToken(@RequestBody RefreshTokenRequest request, HttpServletRequest servletRequest) {
        try {
            AuthResponse response = authServiceImpl.refreshToken(request.getRefreshToken());
            if (response.isSuccess()) {
                return ResponseEntity.ok(
                    CustomResponseEntity.success(HttpStatus.OK.value(), response.getMessage(), response, servletRequest.getRequestURI())
                );
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), response.getMessage(), servletRequest.getRequestURI())
                );
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                CustomResponseEntity.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Token refresh failed: " + e.getMessage(), servletRequest.getRequestURI())
            );
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<CustomResponseEntity<AuthResponse>> verifyEmail(@RequestParam("token") String token, HttpServletRequest servletRequest) {
        AuthResponse response = authServiceImpl.verifyEmail(token);
        if (response.isSuccess()) {
            return ResponseEntity.ok(
                CustomResponseEntity.success(HttpStatus.OK.value(), response.getMessage(), response, servletRequest.getRequestURI())
            );
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                CustomResponseEntity.error(HttpStatus.BAD_REQUEST.value(), response.getMessage(), servletRequest.getRequestURI())
            );
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<CustomResponseEntity<AuthResponse>> resendVerification(@RequestBody EmailRequest request, HttpServletRequest servletRequest) {
        AuthResponse response = authServiceImpl.resendVerificationEmail(request.getTo());
        if (response.isSuccess()) {
            return ResponseEntity.ok(
                CustomResponseEntity.success(HttpStatus.OK.value(), response.getMessage(), response, servletRequest.getRequestURI())
            );
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                CustomResponseEntity.error(HttpStatus.BAD_REQUEST.value(), response.getMessage(), servletRequest.getRequestURI())
            );
        }
    }

    @PostMapping("/initiate-password-reset")
    public ResponseEntity<CustomResponseEntity<AuthResponse>> initiatePasswordReset(@RequestBody EmailRequest request, HttpServletRequest servletRequest) {
        AuthResponse response = authServiceImpl.initiatePasswordReset(request.getTo());
        if (response.isSuccess()) {
            return ResponseEntity.ok(
                CustomResponseEntity.success(HttpStatus.OK.value(), response.getMessage(), response, servletRequest.getRequestURI())
            );
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                CustomResponseEntity.error(HttpStatus.BAD_REQUEST.value(), response.getMessage(), servletRequest.getRequestURI())
            );
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<CustomResponseEntity<AuthResponse>> resetPassword(@RequestBody UserUpdateInfoRequest request, HttpServletRequest servletRequest) {
        AuthResponse response = authServiceImpl.resetPassword(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(
                CustomResponseEntity.success(HttpStatus.OK.value(), response.getMessage(), response, servletRequest.getRequestURI())
            );
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                CustomResponseEntity.error(HttpStatus.BAD_REQUEST.value(), response.getMessage(), servletRequest.getRequestURI())
            );
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<CustomResponseEntity<LogoutResponse>> logout(@RequestHeader("Authorization") String authHeader, HttpServletRequest servletRequest) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        LogoutResponse response = authServiceImpl.logout(token);
        return ResponseEntity.ok(
            CustomResponseEntity.success(HttpStatus.OK.value(), response.getMessage(), response, servletRequest.getRequestURI())
        );
    }

    @PutMapping("/update-user")
    public ResponseEntity<CustomResponseEntity<UserInfoDTO>> updateUser(@RequestBody UserUpdateInfoRequest request, HttpServletRequest servletRequest) {
        UserInfoDTO userInfo = authServiceImpl.updateUserInfo(request);
        return ResponseEntity.ok(
            CustomResponseEntity.success(HttpStatus.OK.value(), "User updated successfully", userInfo, servletRequest.getRequestURI())
        );
    }

    @PutMapping("/change-password")
    public ResponseEntity<CustomResponseEntity<UserInfoDTO>> changePassword(@RequestBody UserUpdateInfoRequest request, HttpServletRequest servletRequest) {
        UserInfoDTO userInfo = authServiceImpl.changePassword(request);
        return ResponseEntity.ok(
            CustomResponseEntity.success(HttpStatus.OK.value(), "Password changed successfully", userInfo, servletRequest.getRequestURI())
        );
    }

    @DeleteMapping("/delete-user/{userId}")
    public ResponseEntity<CustomResponseEntity<UserInfoDTO>> deleteUser(@RequestHeader("Authorization") String authHeader, @PathVariable String userId, HttpServletRequest servletRequest) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        UserInfoDTO userInfo = authServiceImpl.deleteUser(token, userId);
        return ResponseEntity.ok(
            CustomResponseEntity.success(HttpStatus.OK.value(), "User deleted successfully", userInfo, servletRequest.getRequestURI())
        );
    }

    @GetMapping("/user-info")
    public ResponseEntity<CustomResponseEntity<UserInfoDTO>> getUserInfo(@RequestHeader("Authorization") String authHeader, HttpServletRequest servletRequest) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        UserInfoDTO userInfo = authServiceImpl.getUserInfo(token);
        return ResponseEntity.ok(
            CustomResponseEntity.success(HttpStatus.OK.value(), "User info fetched successfully", userInfo, servletRequest.getRequestURI())
        );
    }

    @GetMapping("/all-users")
    public ResponseEntity<CustomResponseEntity<List<UserInfoDTO>>> getAllUsers(@RequestHeader("Authorization") String authHeader, HttpServletRequest servletRequest) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        List<UserInfoDTO> users = authServiceImpl.getAllUsers(token);
        return ResponseEntity.ok(
            CustomResponseEntity.success(HttpStatus.OK.value(), "All users fetched successfully", users, servletRequest.getRequestURI())
        );
    }

    @PutMapping("/update-role/{role}")
    public ResponseEntity<CustomResponseEntity<UserInfoDTO>> updateRoleToUser(@PathVariable String role, @RequestBody UserUpdateInfoRequest request, HttpServletRequest servletRequest) {
        UserInfoDTO userInfo = authServiceImpl.updateRoleToUser(role, request);
        return ResponseEntity.ok(
            CustomResponseEntity.success(HttpStatus.OK.value(), "User role updated successfully", userInfo, servletRequest.getRequestURI())
        );
    }

    @GetMapping("/check-email/{email}")
    public ResponseEntity<CustomResponseEntity<UserInfoDTO>> checkEmailExists(@PathVariable String email, HttpServletRequest servletRequest) {
        UserInfoDTO userInfo = authServiceImpl.checkEmailExists(email);
        return ResponseEntity.ok(
            CustomResponseEntity.success(HttpStatus.OK.value(), "Email check complete", userInfo, servletRequest.getRequestURI())
        );
    }

    @GetMapping("/roles")
    public ResponseEntity<CustomResponseEntity<List<Role>>> getAllRoles(HttpServletRequest servletRequest) {
        List<Role> roles = authServiceImpl.getAllRoles();
        return ResponseEntity.ok(
            CustomResponseEntity.success(HttpStatus.OK.value(), "Roles fetched successfully", roles, servletRequest.getRequestURI())
        );
    }
}
