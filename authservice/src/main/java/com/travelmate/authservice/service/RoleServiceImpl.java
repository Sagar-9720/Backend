package com.travelmate.authservice.service;

import com.travelmate.authservice.dto.*;
import com.travelmate.authservice.entity.Role;
import com.travelmate.authservice.entity.User;
import com.travelmate.authservice.mapper.UserMapper;
import com.travelmate.authservice.repository.RoleRepository;
import com.travelmate.authservice.repository.UserRepository;
import com.travelmate.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import com.travelmate.authservice.exception.UserNotFoundException;
import com.travelmate.authservice.exception.UnauthorizedAccessException;
import com.travelmate.authservice.exception.EmailAlreadyExistException;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final RoleRepository roleRepository;

    @Autowired
    private final JwtUtil jwtUtil;


    @Override
    public UserInfoDTO updateRoleToUser(String token, String role, UserUpdateInfoRequest request) {
        logger.info("Updating role to {} for userId: {}", role, request.userId());
        if (token == null || !jwtUtil.validateToken(token)) {
            logger.warn("Invalid or missing token for updateRoleToUser");
            throw new UnauthorizedAccessException("Invalid or missing token");
        }
        String userRole = jwtUtil.getRoleFromToken(token);
        if (!"ADMIN".equalsIgnoreCase(userRole) && !"SUBADMIN".equalsIgnoreCase(userRole)) {
            logger.warn("Unauthorized access by user role: {}", userRole);
            throw new UnauthorizedAccessException("Only ADMIN or SUBADMIN can update user roles");
        }
        User user = userRepository.findById(Long.parseLong(request.userId())).orElseThrow(() -> new RuntimeException("User not found"));

        Role newRole = roleRepository.findByName(role.toUpperCase()).orElseThrow(() -> new RuntimeException("Role not found"));

        user.setRole(newRole);
        User updatedUser = userRepository.save(user);

        return UserMapper.toUserInfoDTO(updatedUser);
    }

    @Override
    public List<Role> getAllRoles(String token) {
        logger.info("Fetching all roles with token validation");
        if (token == null || !jwtUtil.validateToken(token)) {
            logger.warn("Invalid or missing token for getAllRoles");
            throw new UnauthorizedAccessException("Invalid or missing token");
        }
        String userRole = jwtUtil.getRoleFromToken(token);
        if (!"ADMIN".equalsIgnoreCase(userRole) && !"SUBADMIN".equalsIgnoreCase(userRole)) {
            logger.warn("Unauthorized access by user role: {}", userRole);
            throw new UnauthorizedAccessException("Only ADMIN or SUBADMIN can access roles");
        }
        logger.info("User role {} is authorized to fetch roles", userRole);
        // Fetch all roles from the repository
        List<Role> roles = roleRepository.findAll();
        if (roles.isEmpty()) {
            logger.warn("No roles found in the system");
            throw new RuntimeException("No roles found in the system");

        }
        logger.info("Fetched {} roles from the repository", roles.size());
        return roles;
    }

    @Override
    public Role createRole(String token, Role role) throws UnauthorizedAccessException {
        String userRole = jwtUtil.getRoleFromToken(token);
        if (!"ADMIN".equalsIgnoreCase(userRole) && !"SUBADMIN".equalsIgnoreCase(userRole)) {
            throw new UnauthorizedAccessException("Only ADMIN or SUBADMIN can create roles");
        }
        logger.info("Creating new role: {} by {}", role.getName(), userRole);
        if (roleRepository.findByName(role.getName()).isPresent()) {
            throw new EmailAlreadyExistException("Role already exists: " + role.getName());
        }
        return roleRepository.save(role);
    }

    @Override
    public Role updateRole(String token, Long roleId, Role updatedRole) throws UnauthorizedAccessException, UserNotFoundException {
        String userRole = jwtUtil.getRoleFromToken(token);
        if (!"ADMIN".equalsIgnoreCase(userRole) && !"SUBADMIN".equalsIgnoreCase(userRole)) {
            throw new UnauthorizedAccessException("Only ADMIN or SUBADMIN can update roles");
        }
        logger.info("Updating role with id: {} by {}", roleId, userRole);
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new UserNotFoundException("Role not found with id: " + roleId));
        role.setName(updatedRole.getName());
        return roleRepository.save(role);
    }

    @Override
    public void deleteRole(String token, Long roleId) throws UnauthorizedAccessException, UserNotFoundException {
        // Validate token and check if user is admin
        if (token == null || !jwtUtil.validateToken(token)) {
            throw new UnauthorizedAccessException("Invalid or missing token");
        }
        String userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(Long.parseLong(userId)).orElseThrow(() -> new UserNotFoundException("User not found for token"));
        if (!user.getRole().getName().equalsIgnoreCase("ADMIN")) {
            throw new UnauthorizedAccessException("Only admin can delete roles");
        }
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new UserNotFoundException("Role not found with id: " + roleId));
        roleRepository.delete(role);
        logger.info("Role deleted with id: {}", roleId);
    }

}
