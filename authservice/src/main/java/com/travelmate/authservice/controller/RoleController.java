package com.travelmate.authservice.controller;

import com.travelmate.authservice.dto.*;
import com.travelmate.authservice.entity.Role;
import com.travelmate.authservice.response.CustomResponseEntity;
import com.travelmate.authservice.exception.UserNotFoundException;
import com.travelmate.authservice.exception.UnauthorizedAccessException;
import com.travelmate.authservice.service.RoleServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/auth/roles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RoleController {

    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private final RoleServiceImpl roleServiceImpl;

    @PutMapping("/update/{role}")
    public ResponseEntity<CustomResponseEntity<UserInfoDTO>> updateRoleToUser(@RequestHeader("Authorization") String authHeader, @PathVariable String role, @RequestBody UserUpdateInfoRequest request, HttpServletRequest servletRequest) {
        try {
            logger.info("Assigning role initiated");
            String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized access: No token provided", servletRequest.getRequestURI()));
            }
            UserInfoDTO userInfo = roleServiceImpl.updateRoleToUser(token, role, request);
            return ResponseEntity.ok(CustomResponseEntity.success(HttpStatus.OK.value(), "User role updated successfully", userInfo, servletRequest.getRequestURI()));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomResponseEntity.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), servletRequest.getRequestURI()));
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Role update failed: " + e.getMessage(), servletRequest.getRequestURI()));
        }
    }

    @GetMapping
    public ResponseEntity<CustomResponseEntity<List<Role>>> getAllRoles(@RequestHeader("Authorization") String authHeader, HttpServletRequest servletRequest) {
        try {
            logger.info("Getting all roles initiated");
            String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
            if (token == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), "Unauthorized access: No token provided", servletRequest.getRequestURI()));
            }
            List<Role> roles = roleServiceImpl.getAllRoles(token);
            return ResponseEntity.ok(CustomResponseEntity.success(HttpStatus.OK.value(), "Roles fetched successfully", roles, servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Roles fetch failed: " + e.getMessage(), servletRequest.getRequestURI()));
        }
    }

    @PostMapping
    public ResponseEntity<CustomResponseEntity<Role>> createRole(@RequestHeader("Authorization") String authHeader, @RequestBody Role role, HttpServletRequest servletRequest) {
        try {
            logger.info("Creating role initiated");
            String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
            Role createdRole = roleServiceImpl.createRole(token, role);
            return ResponseEntity.status(HttpStatus.CREATED).body(CustomResponseEntity.success(HttpStatus.CREATED.value(), "Role created successfully", createdRole, servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Role creation failed: " + e.getMessage(), servletRequest.getRequestURI()));
        }
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<CustomResponseEntity<Role>> updateRole(@RequestHeader("Authorization") String authHeader, @PathVariable Long roleId, @RequestBody Role role, HttpServletRequest servletRequest) {
        try {
            logger.info("Updating role initiated");
            String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
            Role updatedRole = roleServiceImpl.updateRole(token, roleId, role);
            return ResponseEntity.ok(CustomResponseEntity.success(HttpStatus.OK.value(), "Role updated successfully", updatedRole, servletRequest.getRequestURI()));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomResponseEntity.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Role update failed: " + e.getMessage(), servletRequest.getRequestURI()));
        }
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<CustomResponseEntity<String>> deleteRole(@RequestHeader("Authorization") String authHeader, @PathVariable Long roleId, HttpServletRequest servletRequest) {
        try {
            logger.info("Deleting role initiated");
            String token = (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : null;
            roleServiceImpl.deleteRole(token, roleId);
            return ResponseEntity.ok(CustomResponseEntity.success(HttpStatus.OK.value(), "Role deleted successfully", null, servletRequest.getRequestURI()));
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(CustomResponseEntity.error(HttpStatus.UNAUTHORIZED.value(), e.getMessage(), servletRequest.getRequestURI()));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(CustomResponseEntity.error(HttpStatus.NOT_FOUND.value(), e.getMessage(), servletRequest.getRequestURI()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(CustomResponseEntity.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Role deletion failed: " + e.getMessage(), servletRequest.getRequestURI()));
        }
    }
}
