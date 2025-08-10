package com.travelmate.authservice.service;

import com.travelmate.authservice.dto.*;
import com.travelmate.authservice.entity.Role;
import com.travelmate.authservice.exception.EmailNotFoundException;
import com.travelmate.authservice.exception.UserNotFoundException;
import com.travelmate.authservice.exception.UnauthorizedAccessException;

import java.util.List;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request);

    AuthResponse registerSubAdmin(RegisterRequest request, String token) throws UnauthorizedAccessException;

    TokenValidationResponse validateToken(String token) throws UnauthorizedAccessException;

    AuthResponse refreshToken(String token) throws UnauthorizedAccessException;

    LogoutResponse logout(String token) throws UnauthorizedAccessException;

    UserInfoDTO updateUserInfo(UserUpdateInfoRequest request) throws UserNotFoundException, UnauthorizedAccessException;

    UserInfoDTO deleteRequest(String token) throws UserNotFoundException, UnauthorizedAccessException;

    UserInfoDTO deleteUser(String token, String userId) throws UserNotFoundException, UnauthorizedAccessException;

    UserInfoDTO changePassword(UserUpdateInfoRequest request) throws UserNotFoundException, UnauthorizedAccessException;

    AuthResponse resetPassword(UserUpdateInfoRequest request) throws EmailNotFoundException;

    UserInfoDTO getUserInfo(String token) throws UserNotFoundException, UnauthorizedAccessException;

    List<UserInfoDTO> getAllUsers(String token) throws UnauthorizedAccessException;

    UserInfoDTO updateRoleToUser(String role, UserUpdateInfoRequest request) throws UserNotFoundException, UnauthorizedAccessException;

    UserInfoDTO checkEmailExists(String email) throws EmailNotFoundException;

    List<Role> getAllRoles(String token);

    AuthResponse initiatePasswordReset(String email) throws EmailNotFoundException;

    AuthResponse verifyEmail(String token) throws UnauthorizedAccessException;

    AuthResponse resendVerificationEmail(String email) throws EmailNotFoundException;

    Role createRole(String token, Role role) throws UnauthorizedAccessException;

    Role updateRole(String token, Long roleId, Role role) throws UnauthorizedAccessException, UserNotFoundException;

    void deleteRole(String token, Long roleId) throws UnauthorizedAccessException, UserNotFoundException;

}
