package com.company.internalmgmt.common.repository;

import com.company.internalmgmt.common.model.SystemConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for SystemConfig entities
 */
@Repository
public interface SystemConfigRepository extends JpaRepository<SystemConfig, Long> {
    
    /**
     * Find system configuration by key
     * 
     * @param configKey the configuration key
     * @return optional containing the system config if found
     */
    Optional<SystemConfig> findByConfigKey(String configKey);
    
    /**
     * Check if a configuration key exists
     * 
     * @param configKey the configuration key
     * @return true if the key exists, false otherwise
     */
    boolean existsByConfigKey(String configKey);
} 