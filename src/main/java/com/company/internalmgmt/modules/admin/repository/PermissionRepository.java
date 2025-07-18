package com.company.internalmgmt.modules.admin.repository;

import com.company.internalmgmt.modules.admin.model.Permission;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
    
    Boolean existsByName(String name);
} 
