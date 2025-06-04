package com.company.internalmgmt.modules.hrm.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.company.internalmgmt.modules.hrm.model.SkillCategory;

/**
 * Repository for managing SkillCategory entities
 */
@Repository
public interface SkillCategoryRepository extends JpaRepository<SkillCategory, Long> {

    /**
     * Find a skill category by its name
     *
     * @param name the category name
     * @return the skill category if found
     */
    Optional<SkillCategory> findByNameIgnoreCase(String name);

    /**
     * Check if a skill category exists with the given name
     *
     * @param name the category name
     * @return true if a category with the given name exists
     */
    boolean existsByNameIgnoreCase(String name);
} 
