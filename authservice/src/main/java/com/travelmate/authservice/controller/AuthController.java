package com.travelmate.authservice.controller;

import com.travelmate.authservice.dto.*;
import com.travelmate.authservice.response.CustomResponseEntity;
import com.travelmate.authservice.service.AuthServiceImpl;
import com.travelmate.authservice.exception.EmailAlreadyExistException;
import com.travelmate.authservice.exception.EmailNotFoundException;
import com.travelmate.authservice.exception.UserNotFoundException;
import com.travelmate.authservice.exception.UnauthorizedAccessException;
import io.micrometer.core.annotation.Timed;
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
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Timed(value = "auth.controller", description = "Auth controller timing metrics")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private final AuthServiceImpl authServiceImpl;

    @PostMapping("/register")
    @Timed(value = "auth.register", description = "Time taken to register a new user")
    public ResponseEntity<CustomResponseEntity<AuthResponse>> register(@Valid @RequestBody RegisterRequest request, HttpServletRequest servletRequest) {
        try {
            AuthResponse response = authServiceImpl.register(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(CustomResponseEntity.success(HttpStatus.CREATED.value(), response.message(), response, servletRequest.getRequestURI()));
        } catch (EmailAlreadyExistException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CustomResponseEntity.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Registration failed: " + e.getMessage(), servletRequest.getRequestURI()));
        }
    }

    @PostMapping("/login")
    @Timed(value = "auth.login", description = "Time taken to login")
    public ResponseEntity<CustomResponseEntity<AuthResponse>> login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        try {
            AuthResponse response = authServiceImpl.login(request);
            return ResponseEntity.ok(CustomResponseEntity.success(HttpStatus.OK.value(), response.message(), response, servletRequest.getRequestURI()));
        } catch (EmailNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomResponseEntity.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), servletRequest.getRequestURI()));
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Login failed: " + e.getMessage(), servletRequest.getRequestURI()));
        }
    }

    @PostMapping("/validate")
    @Timed(value = "auth.validate", description = "Time taken to validate a token")
    public ResponseEntity<CustomResponseEntity<TokenValidationResponse>> validateToken(@RequestHeader("Authorization") String authHeader, HttpServletRequest servletRequest) {
        try {
            logger.info("Validating token from request: {}", servletRequest.getRequestURI());
            logger.debug("Authorization header: {}", authHeader);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                TokenValidationResponse response = new TokenValidationResponse(false, null, null, null, null, "Invalid authorization header");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), response.message(), servletRequest.getRequestURI()));
            }
            String token = authHeader.substring(7);
            TokenValidationResponse response = authServiceImpl.validateToken(token);
            return ResponseEntity.ok(CustomResponseEntity.success(HttpStatus.OK.value(), response.message(), response, servletRequest.getRequestURI()));
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Token validation failed", servletRequest.getRequestURI()));
        }
    }

    @PostMapping("/refresh")
    @Timed(value = "auth.refresh", description = "Time taken to refresh a token")
    public ResponseEntity<CustomResponseEntity<AuthResponse>> refreshToken(@RequestHeader("Authorization") String authHeader, HttpServletRequest servletRequest) {
        try {
            logger.info("Validating token from request: {}", servletRequest.getRequestURI());
            logger.debug("Authorization header: {}", authHeader);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                TokenValidationResponse response = new TokenValidationResponse(false, null, null, null, null, "Invalid authorization header");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), response.message(), servletRequest.getRequestURI()));
            }
            String refreshToken = authHeader.substring(7);
            AuthResponse response = authServiceImpl.refreshToken(refreshToken);
            return ResponseEntity.ok(CustomResponseEntity.success(HttpStatus.OK.value(), response.message(), response, servletRequest.getRequestURI()));
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), servletRequest.getRequestURI()));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomResponseEntity.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Token refresh failed: " + e.getMessage(), servletRequest.getRequestURI()));
        }
    }

    @GetMapping("/verify-email")
    @Timed(value = "auth.verifyEmail", description = "Time taken to verify email")
    public ResponseEntity<CustomResponseEntity<AuthResponse>> verifyEmail(@RequestHeader("Authorization") String authHeader, HttpServletRequest servletRequest) {
        try {
            logger.info("Validating token from request: {}", servletRequest.getRequestURI());
            logger.debug("Authorization header: {}", authHeader);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                TokenValidationResponse response = new TokenValidationResponse(false, null, null, null, null, "Invalid authorization header");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), response.message(), servletRequest.getRequestURI()));
            }
            String token = authHeader.substring(7);
            AuthResponse response = authServiceImpl.verifyEmail(token);
            return ResponseEntity.ok(CustomResponseEntity.success(HttpStatus.OK.value(), response.message(), response, servletRequest.getRequestURI()));
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Email verification failed: " + e.getMessage(), servletRequest.getRequestURI()));
        }
    }

    @PostMapping("/resend-verification")
    @Timed(value = "auth.resendVerification", description = "Time taken to resend verification email")
    public ResponseEntity<CustomResponseEntity<AuthResponse>> resendVerification(@RequestHeader("Authorization") String authHeader, HttpServletRequest servletRequest) {
        try {
            logger.info("Resending verification email for request: {}", servletRequest.getRequestURI());
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                TokenValidationResponse response = new TokenValidationResponse(false, null, null, null, null, "Invalid authorization header");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), response.message(), servletRequest.getRequestURI()));
            }
            String token = authHeader.substring(7);
            AuthResponse response = authServiceImpl.resendVerificationEmail(token);
            return ResponseEntity.ok(CustomResponseEntity.success(HttpStatus.OK.value(), response.message(), response, servletRequest.getRequestURI()));
        } catch (EmailNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomResponseEntity.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), servletRequest.getRequestURI()));
        } catch (EmailAlreadyExistException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CustomResponseEntity.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Resend verification failed: " + e.getMessage(), servletRequest.getRequestURI()));
        }
    }

    @PostMapping("/reset-password-request")
    @Timed(value = "auth.resetPasswordRequest", description = "Time taken to request password reset")
    public ResponseEntity<CustomResponseEntity<AuthResponse>> resetPasswordRequest(@RequestBody String email, HttpServletRequest servletRequest) {
        AuthResponse response = authServiceImpl.resetPasswordRequest(email);
        if (response.success()) {
            return ResponseEntity.ok(CustomResponseEntity.success(HttpStatus.OK.value(), response.message(), response, servletRequest.getRequestURI()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CustomResponseEntity.error(HttpStatus.BAD_REQUEST.value(), response.message(), servletRequest.getRequestURI()));
        }
    }

    @PostMapping("/reset-password")
    @Timed(value = "auth.resetPassword", description = "Time taken to reset password")
    public ResponseEntity<CustomResponseEntity<AuthResponse>> resetPassword(@RequestBody ResetPasswordRequest request, HttpServletRequest servletRequest) {
        AuthResponse response = authServiceImpl.resetPassword(request.token(), request.password());
        if (response.success()) {
            return ResponseEntity.ok(CustomResponseEntity.success(HttpStatus.OK.value(), response.message(), response, servletRequest.getRequestURI()));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(CustomResponseEntity.error(HttpStatus.BAD_REQUEST.value(), response.message(), servletRequest.getRequestURI()));
        }
    }

    @PostMapping("/logout")
    @Timed(value = "auth.logout", description = "Time taken to logout")
    public ResponseEntity<CustomResponseEntity<LogoutResponse>> logout(@RequestHeader("Authorization") String authHeader, HttpServletRequest servletRequest) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        LogoutResponse response = authServiceImpl.logout(token);
        return ResponseEntity.ok(CustomResponseEntity.success(HttpStatus.OK.value(), response.message(), response, servletRequest.getRequestURI()));
    }

    @PutMapping("/update-user")
    @Timed(value = "auth.updateUser", description = "Time taken to update user information")
    public ResponseEntity<CustomResponseEntity<UserInfoDTO>> updateUser(@RequestHeader("Authorization") String authHeader, @RequestBody UserUpdateInfoRequest request, HttpServletRequest servletRequest) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized access: No token provided", servletRequest.getRequestURI()));
        }
        try {
            UserInfoDTO userInfo = authServiceImpl.updateUserInfo(token, request);
            return ResponseEntity.ok(CustomResponseEntity.success(HttpStatus.OK.value(), "User updated successfully", userInfo, servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "User update failed: " + e.getMessage(), servletRequest.getRequestURI()));
        }
    }

    @PutMapping("/change-password")
    @Timed(value = "auth.changePassword", description = "Time taken to change password")
    public ResponseEntity<CustomResponseEntity<UserInfoDTO>> changePassword(@RequestHeader("Authorization") String authHeader, @RequestBody UserUpdateInfoRequest request, HttpServletRequest servletRequest) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized access: No token provided", servletRequest.getRequestURI()));
        }
        try {
            UserInfoDTO userInfo = authServiceImpl.changePassword(token, request);
            return ResponseEntity.ok(CustomResponseEntity.success(HttpStatus.OK.value(), "Password changed successfully", userInfo, servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "User update failed: " + e.getMessage(), servletRequest.getRequestURI()));
        }

    }

    @DeleteMapping("/delete-user/{userId}")
    @Timed(value = "auth.deleteUser", description = "Time taken to delete a user")
    public ResponseEntity<CustomResponseEntity<UserInfoDTO>> deleteUser(@RequestHeader("Authorization") String authHeader, @PathVariable String userId, HttpServletRequest servletRequest) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        UserInfoDTO userInfo = authServiceImpl.deleteUser(token, userId);
        return ResponseEntity.ok(CustomResponseEntity.success(HttpStatus.OK.value(), "User deleted successfully", userInfo, servletRequest.getRequestURI()));
    }

    @GetMapping("/user-info")
    @Timed(value = "auth.getUserInfo", description = "Time taken to fetch user information")
    public ResponseEntity<CustomResponseEntity<UserInfoDTO>> getUserInfo(@RequestHeader("Authorization") String authHeader, HttpServletRequest servletRequest) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        UserInfoDTO userInfo = authServiceImpl.getUserInfo(token);
        return ResponseEntity.ok(CustomResponseEntity.success(HttpStatus.OK.value(), "User info fetched successfully", userInfo, servletRequest.getRequestURI()));
    }

    @GetMapping("/all-users")
    @Timed(value = "auth.getAllUsers", description = "Time taken to fetch all users")
    public ResponseEntity<CustomResponseEntity<List<UserInfoDTO>>> getAllUsers(@RequestHeader("Authorization") String authHeader, HttpServletRequest servletRequest) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        List<UserInfoDTO> users = authServiceImpl.getAllUsers(token);
        return ResponseEntity.ok(CustomResponseEntity.success(HttpStatus.OK.value(), "All users fetched successfully", users, servletRequest.getRequestURI()));
    }


    @GetMapping("/check-email/{email}")
    @Timed(value = "auth.checkEmailExists", description = "Time taken to check if email exists")
    public ResponseEntity<CustomResponseEntity<UserInfoDTO>> checkEmailExists(@PathVariable String email, HttpServletRequest servletRequest) {
        try {
            UserInfoDTO userInfo = authServiceImpl.checkEmailExists(email);
            return ResponseEntity.ok(CustomResponseEntity.success(HttpStatus.OK.value(), "Email check complete", userInfo, servletRequest.getRequestURI()));
        } catch (EmailNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomResponseEntity.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Email check failed: " + e.getMessage(), servletRequest.getRequestURI()));
        }
    }


    @PostMapping("/register-subadmin")
    @Timed(value = "auth.registerSubAdmin", description = "Time taken to register a new subadmin")
    public ResponseEntity<CustomResponseEntity<AuthResponse>> registerSubAdmin(@Valid @RequestBody RegisterRequest request, @RequestHeader("Authorization") String authHeader, HttpServletRequest servletRequest) {
        try {
            String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
            AuthResponse response = authServiceImpl.registerSubAdmin(request, token);
            return ResponseEntity.status(HttpStatus.CREATED).body(CustomResponseEntity.success(HttpStatus.CREATED.value(), response.message(), response, servletRequest.getRequestURI()));
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SubAdmin registration failed: " + e.getMessage(), servletRequest.getRequestURI()));
        }
    }


    @PutMapping("/delete-request")
    @Timed(value = "auth.deleteRequest", description = "Time taken to submit a delete request")
    public ResponseEntity<CustomResponseEntity<UserInfoDTO>> deleteRequest(@RequestHeader("Authorization") String authHeader, HttpServletRequest servletRequest) {
        String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
        try {
            UserInfoDTO userInfo = authServiceImpl.deleteRequest(token);
            return ResponseEntity.ok(CustomResponseEntity.success(HttpStatus.OK.value(), "Delete request submitted successfully", userInfo, servletRequest.getRequestURI()));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomResponseEntity.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), servletRequest.getRequestURI()));
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Delete request failed: " + e.getMessage(), servletRequest.getRequestURI()));
        }
    }

    @PutMapping("/update-role")
    @Timed(value = "auth.updateRole", description = "Time taken to update user role")
    public ResponseEntity<CustomResponseEntity<UserInfoDTO>> updateRoleToUser(@RequestHeader("Authorization") String authHeader, @RequestBody UserUpdateInfoRequest request, HttpServletRequest servletRequest) {
        try {
            logger.info("Assigning role initiated");
            String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized access: No token provided", servletRequest.getRequestURI()));
            }
            UserInfoDTO userInfo = authServiceImpl.updateRoleToUser(token, request);
            return ResponseEntity.ok(CustomResponseEntity.success(HttpStatus.OK.value(), "User role updated successfully", userInfo, servletRequest.getRequestURI()));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomResponseEntity.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), servletRequest.getRequestURI()));
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Role update failed: " + e.getMessage(), servletRequest.getRequestURI()));
        }
    }

    @GetMapping("/get-user-name")
    @Timed(value = "auth.getUserName", description = "Time taken to fetch user names")
    public ResponseEntity<CustomResponseEntity<String>> getUsersName(@RequestHeader("Authorization") String authHeader, @RequestBody List<String> userIds, HttpServletRequest servletRequest) {
        try {
            String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized access: No token provided", servletRequest.getRequestURI()));
            }
            List<Map<String, String>> userName = authServiceImpl.getUserNameThroughId(token, userIds);
            return ResponseEntity.ok(CustomResponseEntity.success(HttpStatus.OK.value(), "User name fetched successfully", userName.toString(), servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch user name: " + e.getMessage(), servletRequest.getRequestURI()));
        }
    }

    @GetMapping("/all-subadmins")
    @Timed(value = "auth.getAllSubAdmins", description = "Time taken to fetch all subadmins")
    public ResponseEntity<CustomResponseEntity<List<UserInfoDTO>>> getAllSubAdmins(@RequestHeader("Authorization") String authHeader, HttpServletRequest servletRequest) {
        try {
            String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized access: No token provided", servletRequest.getRequestURI()));
            }
            List<UserInfoDTO> subAdmins = authServiceImpl.getAllSubAdmins(token);
            return ResponseEntity.ok(CustomResponseEntity.success(HttpStatus.OK.value(), "All subadmins fetched successfully", subAdmins, servletRequest.getRequestURI()));
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch subadmins: " + e.getMessage(), servletRequest.getRequestURI()));
        }
    }

    @GetMapping("/all-delete-requested-users")
    @Timed(value = "auth.getAllDeleteRequestedUsers", description = "Time taken to fetch all delete requested users")
    public ResponseEntity<CustomResponseEntity<List<UserInfoDTO>>> getAllDeleteRequestedUsers(@RequestHeader("Authorization") String authHeader, HttpServletRequest servletRequest) {
        try {
            String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized access: No token provided", servletRequest.getRequestURI()));
            }
            List<UserInfoDTO> deleteRequestedUsers = authServiceImpl.getALlDeleteRequestedUsers(token);
            return ResponseEntity.ok(CustomResponseEntity.success(HttpStatus.OK.value(), "All delete requested users fetched successfully", deleteRequestedUsers, servletRequest.getRequestURI()));
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to fetch delete requested users: " + e.getMessage(), servletRequest.getRequestURI()));
        }
    }
}