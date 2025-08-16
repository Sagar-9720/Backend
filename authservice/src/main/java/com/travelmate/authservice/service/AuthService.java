package com.travelmate.authservice.service;

import com.travelmate.authservice.dto.*;
import com.travelmate.authservice.exception.EmailNotFoundException;
import com.travelmate.authservice.exception.UserNotFoundException;
import com.travelmate.authservice.exception.UnauthorizedAccessException;

import java.util.List;
import java.util.Map;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request);

    AuthResponse refreshToken(String token) throws UnauthorizedAccessException;

    AuthResponse registerSubAdmin(RegisterRequest request, String token) throws UnauthorizedAccessException;

    TokenValidationResponse validateToken(String token) throws UnauthorizedAccessException;

    LogoutResponse logout(String token) throws UnauthorizedAccessException;

    UserInfoDTO updateUserInfo(String token, UserUpdateInfoRequest request) throws UserNotFoundException, UnauthorizedAccessException;

    UserInfoDTO deleteRequest(String token) throws UserNotFoundException, UnauthorizedAccessException;

    UserInfoDTO deleteUser(String token, String userId) throws UserNotFoundException, UnauthorizedAccessException;

    UserInfoDTO changePassword(String token, UserUpdateInfoRequest request) throws UserNotFoundException, UnauthorizedAccessException;

    UserInfoDTO getUserInfo(String token) throws UserNotFoundException, UnauthorizedAccessException;

    List<UserInfoDTO> getAllUsers(String token) throws UnauthorizedAccessException;

    UserInfoDTO checkEmailExists(String email) throws EmailNotFoundException;

    AuthResponse verifyEmail(String token) throws UnauthorizedAccessException;

    AuthResponse resendVerificationEmail(String email) throws EmailNotFoundException;

    AuthResponse resetPasswordRequest(String email) throws EmailNotFoundException;

    AuthResponse resetPassword(String token, String newPassword) throws EmailNotFoundException;

    UserInfoDTO updateRoleToUser(String token, UserUpdateInfoRequest request) throws UserNotFoundException, UnauthorizedAccessException;

    List<Map<String, String>> getUserNameThroughId(String token, List<String> userIds);
}
