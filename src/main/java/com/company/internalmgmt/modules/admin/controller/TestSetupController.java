package com.company.internalmgmt.modules.admin.controller;

import com.company.internalmgmt.modules.admin.model.Role;
import com.company.internalmgmt.modules.admin.model.User;
import com.company.internalmgmt.modules.admin.repository.RoleRepository;
import com.company.internalmgmt.modules.admin.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/test-setup")
public class TestSetupController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/create-data")
    public ResponseEntity<String> createTestData() {
        try {
            // Xóa dữ liệu hiện có
            userRepository.findByUsername("admin").ifPresent(user -> {
                userRepository.delete(user);
            });

            // Tạo role ADMIN nếu chưa tồn tại
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName("ROLE_ADMIN");
                        newRole.setDescription("Administrator Role");
                        newRole.setCreatedAt(LocalDateTime.now());
                        newRole.setUpdatedAt(LocalDateTime.now());
                        return roleRepository.save(newRole);
                    });

            // Tạo user admin
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@company.com");
            admin.setFullName("Administrator");
            admin.setPassword(passwordEncoder.encode("123456"));

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            admin.setRoles(roles);

            userRepository.save(admin);

            return ResponseEntity.ok("Test data created successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error creating test data: " + e.getMessage());
        }
    }
} 
