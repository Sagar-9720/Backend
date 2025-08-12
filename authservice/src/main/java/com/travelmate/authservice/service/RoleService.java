package com.travelmate.authservice.service;

import com.travelmate.authservice.dto.*;
import com.travelmate.authservice.entity.Role;
import com.travelmate.authservice.exception.UserNotFoundException;
import com.travelmate.authservice.exception.UnauthorizedAccessException;

import java.util.List;

public interface RoleService {

    UserInfoDTO updateRoleToUser(String token, String role, UserUpdateInfoRequest request) throws UserNotFoundException, UnauthorizedAccessException;

    List<Role> getAllRoles(String token);

    Role createRole(String token, Role role) throws UnauthorizedAccessException;

    Role updateRole(String token, Long roleId, Role role) throws UnauthorizedAccessException, UserNotFoundException;

    void deleteRole(String token, Long roleId) throws UnauthorizedAccessException, UserNotFoundException;

}
