package com.company.internalmgmt;

import com.company.internalmgmt.modules.admin.model.Permission;
import com.company.internalmgmt.modules.admin.model.Role;
import com.company.internalmgmt.modules.admin.model.User;
import com.company.internalmgmt.modules.admin.repository.PermissionRepository;
import com.company.internalmgmt.modules.admin.repository.RoleRepository;
import com.company.internalmgmt.modules.admin.repository.UserRepository;

import com.company.internalmgmt.config.TestDataInitializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TestDataInitializer testDataInitializer;

    @GetMapping("/create-admin-test")
    public String createAdminUser() {
        try {
            // Xóa user admin cũ nếu tồn tại
            userRepository.findByUsername("admin").ifPresent(user -> {
                userRepository.delete(user);
            });

            // Tạo permission nếu chưa tồn tại
            createBasicPermissions();

            // Tạo role ADMIN nếu chưa tồn tại
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName("ROLE_ADMIN");
                        newRole.setDescription("Administrator Role");
                        newRole.setCreatedAt(LocalDateTime.now());
                        newRole.setUpdatedAt(LocalDateTime.now());
                        
                        // Gán tất cả permission cho admin
                        Set<Permission> permissions = new HashSet<>(permissionRepository.findAll());
                        newRole.setPermissions(permissions);
                        
                        return roleRepository.save(newRole);
                    });

            // Tạo user admin
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@company.com");
            admin.setFullName("Administrator");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setEnabled(true);
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);
            admin.setRoles(roles);

            userRepository.save(admin);

            // Khởi tạo dữ liệu test
            testDataInitializer.initializeTestData();
            
            return "Dữ liệu test đã được khởi tạo thành công! Admin username: admin, Password: 123456";
        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi khởi tạo dữ liệu test: " + e.getMessage();
        }
    }
    
    @PostMapping("/test-permissions")
    public ResponseEntity<Map<String, Object>> testPermissions(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        User user = userRepository.findByUsername(username).orElse(null);
        
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", "User không tồn tại"
            ));
        }
        
        List<String> permissions = user.getRoles().stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(Permission::getName)
            .distinct()
            .toList();
        
        return ResponseEntity.ok(Map.of(
            "status", "success", 
            "username", username,
            "roles", user.getRoles().stream().map(Role::getName).toList(),
            "permissions", permissions
        ));
    }
    
    private void createBasicPermissions() {
        // Opportunity permissions
        createPermissionIfNotExists("opportunity:read:all", "Can view all opportunities");
        createPermissionIfNotExists("opportunity:create", "Can create opportunities");
        createPermissionIfNotExists("opportunity:update:all", "Can update all opportunities");
        createPermissionIfNotExists("opportunity:update:own", "Can update own opportunities");
        createPermissionIfNotExists("opportunity:update:assigned", "Can update assigned opportunities");
        createPermissionIfNotExists("opportunity:delete", "Can delete opportunities");
        
        // Note permissions
        createPermissionIfNotExists("opportunity-note:read:all", "Can view all opportunity notes");
        createPermissionIfNotExists("opportunity-note:create:all", "Can create opportunity notes");
        createPermissionIfNotExists("opportunity-note:update:all", "Can update all opportunity notes");
        createPermissionIfNotExists("opportunity-note:read:assigned", "Can view notes for assigned opportunities");
        createPermissionIfNotExists("opportunity-note:create:assigned", "Can create notes for assigned opportunities");
        createPermissionIfNotExists("opportunity-note:update:assigned", "Can update notes for assigned opportunities");
        
        // Onsite priority permissions
        createPermissionIfNotExists("opportunity-onsite:update:all", "Can update onsite priority for all opportunities");
        createPermissionIfNotExists("opportunity-onsite:update:assigned", "Can update onsite priority for assigned opportunities");
        
        // Sync permissions
        createPermissionIfNotExists("opportunity:sync", "Can synchronize opportunities with Hubspot");
        createPermissionIfNotExists("opportunity-log:read:all", "Can view opportunity sync logs");
        
        // Opportunity assignment permissions
        createPermissionIfNotExists("opportunity-assign:update:all", "Can assign leaders to opportunities");
    }
    
    private Permission createPermissionIfNotExists(String name, String description) {
        return permissionRepository.findByName(name).orElseGet(() -> {
            Permission permission = new Permission();
            permission.setName(name);
            permission.setDescription(description);
            return permissionRepository.save(permission);
        });
    }
} 
