package com.travelmate.authservice.service;

import com.travelmate.authservice.dto.*;
import com.travelmate.authservice.entity.Role;

import java.util.List;

public interface AuthService {

    AuthResponse login(LoginRequest request);
    AuthResponse register(RegisterRequest request);

    TokenValidationResponse validateToken(String token);

    AuthResponse refreshToken(String token);

    LogoutResponse logout(String token);
    UserInfoDTO updateUserInfo(UserUpdateInfoRequest request);
    UserInfoDTO deleteUser(String token, String userId);
    UserInfoDTO changePassword(UserUpdateInfoRequest request);
    AuthResponse resetPassword(UserUpdateInfoRequest request);
    UserInfoDTO getUserInfo(String token);
    List<UserInfoDTO> getAllUsers(String token);
    UserInfoDTO updateRoleToUser(String role, UserUpdateInfoRequest request);
    UserInfoDTO checkEmailExists(String email);
    List<Role> getAllRoles();
    AuthResponse initiatePasswordReset(String email);
    AuthResponse verifyEmail(String token);
    AuthResponse resendVerificationEmail(String email);

}
