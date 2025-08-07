package com.travelmate.authservice.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.travelmate.authservice.entity.Role;
import com.travelmate.authservice.repository.RoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Initialize default roles if they don't exist
        initializeRoles();
    }

    private void initializeRoles() {
        createRoleIfNotExists("USER", "Standard user role");
        createRoleIfNotExists("ADMIN", "Administrator role");
        createRoleIfNotExists("SUBADMIN", "Guest user role");
    }

    private void createRoleIfNotExists(String roleName, String description) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = new Role();
            role.setName(roleName);
            role.setDescription(description);
            roleRepository.save(role);
            log.info("Created role: {}", roleName);
        }
    }
}
