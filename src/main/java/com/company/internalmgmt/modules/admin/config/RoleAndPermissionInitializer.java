package com.company.internalmgmt.modules.admin.config;

import com.company.internalmgmt.modules.admin.model.Permission;
import com.company.internalmgmt.modules.admin.model.Role;
import com.company.internalmgmt.modules.admin.repository.PermissionRepository;
import com.company.internalmgmt.modules.admin.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Initialize roles and permissions on application startup
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RoleAndPermissionInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing roles and permissions...");
        
        // Initialize all permissions first
        initializePermissions();
        
        // Then initialize roles with their permissions
        initializeRoles();
        
        log.info("Roles and permissions initialized successfully");
    }

    private void initializePermissions() {
        // HRM Module Permissions
        createPermissionsIfNotExist(Arrays.asList(
            createPermission("employee:read:all", "View all employee information"),
            createPermission("employee:read:team", "View team employee information"),
            createPermission("employee:read:basic", "View basic employee information"),
            createPermission("employee:read:own", "View own information"),
            createPermission("employee:create", "Create new employee"),
            createPermission("employee:update:all", "Update all employee information"),
            createPermission("employee:update:team", "Update team employee information"),
            createPermission("employee:update:own", "Update own information"),
            createPermission("employee:delete", "Delete employee (soft delete)"),
            createPermission("employee:import", "Import employee list from file"),
            createPermission("employee:export", "Export employee list")
            // ... Add other HRM permissions
        ));

        // Margin Module Permissions
        createPermissionsIfNotExist(Arrays.asList(
            createPermission("employee-cost:read:all", "View all employee costs"),
            createPermission("employee-cost:read:team", "View team employee costs"),
            createPermission("employee-cost:create", "Create employee cost"),
            createPermission("employee-cost:update:all", "Update all employee costs"),
            createPermission("employee-cost:delete", "Delete employee cost"),
            createPermission("employee-cost:import", "Import employee costs from file")
            // ... Add other Margin permissions
        ));

        // Opportunity Module Permissions
        createPermissionsIfNotExist(Arrays.asList(
            createPermission("opportunity:read:all", "View all business opportunities"),
            createPermission("opportunity:create", "Create business opportunity"),
            createPermission("opportunity:update:all", "Update all opportunities"),
            createPermission("opportunity:update:own", "Update own opportunities"),
            createPermission("opportunity:update:assigned", "Update assigned opportunities"),
            createPermission("opportunity:delete", "Delete opportunity")
            // ... Add other Opportunity permissions
        ));

        // Contract Module Permissions
        createPermissionsIfNotExist(Arrays.asList(
            createPermission("contract:read:all", "View all contracts"),
            createPermission("contract:read:own", "View own contracts"),
            createPermission("contract:read:assigned", "View assigned contracts"),
            createPermission("contract:create", "Create new contract"),
            createPermission("contract:update:all", "Update all contracts"),
            createPermission("contract:update:own", "Update own contracts"),
            createPermission("contract:delete", "Delete contract")
            // ... Add other Contract permissions
        ));

        // System Admin Permissions
        createPermissionsIfNotExist(Arrays.asList(
            createPermission("user:read", "View user list"),
            createPermission("user:create", "Create new user"),
            createPermission("user:update", "Update user information"),
            createPermission("user:delete", "Delete user"),
            createPermission("role:read", "View role list"),
            createPermission("role:create", "Create new role"),
            createPermission("role:update", "Update role"),
            createPermission("role:delete", "Delete role"),
            createPermission("permission:read", "View permission list"),
            createPermission("permission:assign", "Assign permissions to roles")
            // ... Add other System Admin permissions
        ));
    }

    private void initializeRoles() {
        // Get all permissions
        Map<String, Permission> permissionMap = new HashMap<>();
        permissionRepository.findAll().forEach(permission -> 
            permissionMap.put(permission.getName(), permission));

        // Admin Role
        Role adminRole = createRole("Admin", "System Administrator with full access");
        adminRole.getPermissions().addAll(permissionMap.values());
        roleRepository.save(adminRole);

        // Division Manager Role
        Role divisionManagerRole = createRole("Division Manager", "Division/Department Manager");
        addPermissionsByPrefix(divisionManagerRole, permissionMap, Arrays.asList(
            "employee:read:all",
            "employee:create",
            "employee:update:all",
            "employee:delete",
            "employee:import",
            "employee:export",
            "employee-cost:read:all",
            "employee-cost:create",
            "opportunity:read:all",
            "contract:read:all",
            "contract:create"
            // ... Add other Division Manager permissions
        ));
        roleRepository.save(divisionManagerRole);

        // Leader Role
        Role leaderRole = createRole("Leader", "Team Leader");
        addPermissionsByPrefix(leaderRole, permissionMap, Arrays.asList(
            "employee:read:team",
            "employee:update:team",
            "employee-cost:read:team",
            "opportunity:read:all",
            "opportunity:update:assigned",
            "contract:read:assigned"
            // ... Add other Leader permissions
        ));
        roleRepository.save(leaderRole);

        // Sales Role
        Role salesRole = createRole("Sales", "Sales Staff");
        addPermissionsByPrefix(salesRole, permissionMap, Arrays.asList(
            "employee:read:basic",
            "opportunity:read:all",
            "opportunity:create",
            "opportunity:update:own",
            "contract:read:own",
            "contract:create"
            // ... Add other Sales permissions
        ));
        roleRepository.save(salesRole);

        // Employee Role
        Role employeeRole = createRole("Employee", "Regular Employee");
        addPermissionsByPrefix(employeeRole, permissionMap, Arrays.asList(
            "employee:read:own",
            "employee:update:own"
            // ... Add other Employee permissions
        ));
        roleRepository.save(employeeRole);
    }

    private Permission createPermission(String name, String description) {
        Permission permission = new Permission();
        permission.setName(name);
        permission.setDescription(description);
        return permission;
    }

    private void createPermissionsIfNotExist(List<Permission> permissions) {
        for (Permission permission : permissions) {
            if (!permissionRepository.existsByName(permission.getName())) {
                permissionRepository.save(permission);
            }
        }
    }

    private Role createRole(String name, String description) {
        Role role = roleRepository.findByName(name)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(name);
                    newRole.setDescription(description);
                    return newRole;
                });
        return role;
    }

    private void addPermissionsByPrefix(Role role, Map<String, Permission> permissionMap, List<String> permissionNames) {
        for (String permissionName : permissionNames) {
            Permission permission = permissionMap.get(permissionName);
            if (permission != null) {
                role.getPermissions().add(permission);
            }
        }
    }
} 