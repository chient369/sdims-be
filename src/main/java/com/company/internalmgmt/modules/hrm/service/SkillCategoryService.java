package com.company.internalmgmt.modules.hrm.service;

import java.util.List;

import com.company.internalmgmt.modules.hrm.dto.SkillCategoryDto;
import com.company.internalmgmt.modules.hrm.dto.SkillCategoryRequest;

/**
 * Service interface for managing skill categories
 */
public interface SkillCategoryService {
    
    /**
     * Find all skill categories
     * 
     * @return list of skill category DTOs
     */
    List<SkillCategoryDto> findAll();
    
    /**
     * Find a skill category by ID
     * 
     * @param id the category ID
     * @return the skill category DTO
     */
    SkillCategoryDto findById(Long id);
    
    /**
     * Find a skill category by name
     * 
     * @param name the category name
     * @return the skill category DTO
     */
    SkillCategoryDto findByName(String name);
    
    /**
     * Create a new skill category
     * 
     * @param categoryRequest the skill category request
     * @return the created skill category DTO
     */
    SkillCategoryDto create(SkillCategoryRequest categoryRequest);
    
    /**
     * Update a skill category
     * 
     * @param id the category ID
     * @param categoryRequest the skill category request
     * @return the updated skill category DTO
     */
    SkillCategoryDto update(Long id, SkillCategoryRequest categoryRequest);
    
    /**
     * Delete a skill category
     * 
     * @param id the category ID
     */
    void delete(Long id);
    
    /**
     * Check if a skill category exists with the given name
     * 
     * @param name the category name
     * @return true if a category with the given name exists
     */
    boolean existsByName(String name);
} 