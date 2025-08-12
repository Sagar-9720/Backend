package com.travelmate.authservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.travelmate.authservice.entity.Role;
import com.travelmate.authservice.entity.User;
import com.travelmate.authservice.repository.RoleRepository;
import com.travelmate.authservice.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:admin@travelmate.com}")
    private String adminEmail;
    @Value("${app.admin.password:Travelmate123}")
    private String adminPassword;

    @Override
    public void run(String... args) throws Exception {
        // Initialize default roles if they don't exist 
        initializeRoles();
        // Initialize default admin user
        initializeAdminUser();
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

    private void initializeAdminUser() {
        if (adminEmail == null || adminPassword == null) {
            log.warn("Admin email or password not set in properties or environment variables");
            return;
        }
        if (!userRepository.existsByEmail(adminEmail)) {
            Role adminRole = roleRepository.findByName("ADMIN").orElseThrow(() -> new RuntimeException("ADMIN role not found"));
            User admin = User.builder().name("Admin").email(adminEmail).password(passwordEncoder.encode(adminPassword)).emailVerified(true).profileImg(null).gender(null).dob(null).phone(null).build();
            admin.setRole(adminRole);
            userRepository.save(admin);
            log.info("Created default admin user: {}", adminEmail);
        }
    }
}
