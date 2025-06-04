package com.company.internalmgmt.modules.hrm.service;

import com.company.internalmgmt.modules.hrm.model.Skill;
import com.company.internalmgmt.modules.hrm.model.SkillCategory;
import com.company.internalmgmt.modules.hrm.dto.SkillRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

import com.company.internalmgmt.modules.hrm.dto.SkillDto;

/**
 * Service interface for managing skills
 */
public interface SkillService {

    /**
     * Get all skill categories
     * @return List of all skill categories
     */
    @PreAuthorize("hasAuthority('skill-category:read')")
    List<SkillCategory> getAllCategories();

    /**
     * Get skill category by ID
     * @param id the category ID
     * @return the skill category
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if not found
     */
    @PreAuthorize("hasAuthority('skill-category:read')")
    SkillCategory getCategoryById(Long id);

    /**
     * Create a new skill category
     * @param category the category to create
     * @return the created category
     * @throws com.company.internalmgmt.common.exception.ResourceAlreadyExistsException if category name already exists
     */
    @PreAuthorize("hasAuthority('skill-category:create')")
    SkillCategory createCategory(SkillCategory category);

    /**
     * Update an existing skill category
     * @param id the category ID
     * @param category the updated category
     * @return the updated category
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if not found
     * @throws com.company.internalmgmt.common.exception.ResourceAlreadyExistsException if category name already exists
     */
    @PreAuthorize("hasAuthority('skill-category:update')")
    SkillCategory updateCategory(Long id, SkillCategory category);

    /**
     * Delete a skill category
     * @param id the category ID
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if not found
     * @throws com.company.internalmgmt.common.exception.ResourceInUseException if category is being used by skills
     */
    @PreAuthorize("hasAuthority('skill-category:delete')")
    void deleteCategory(Long id);

    /**
     * Get all skills (optionally filtered by category)
     * @param categoryId filter by category ID (optional)
     * @return List of skills
     */
    @PreAuthorize("hasAuthority('skill:read')")
    List<Skill> getAllSkills(Long categoryId);

    /**
     * Get skills by category ID
     * @param categoryId the category ID
     * @return List of skills in the category
     */
    @PreAuthorize("hasAuthority('skill:read')")
    List<Skill> getSkillsByCategoryId(Long categoryId);

    /**
     * Get skills with pagination and search
     * @param categoryId filter by category ID (optional)
     * @param search search term for name (optional)
     * @param pageable pagination information
     * @return Page of skills
     */
    @PreAuthorize("hasAuthority('skill:read')")
    Page<Skill> getSkills(Long categoryId, String search, Pageable pageable);

    /**
     * Get skill by ID
     * @param id the skill ID
     * @return the skill
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if not found
     */
    @PreAuthorize("hasAuthority('skill:read')")
    Skill getSkillById(Long id);

    /**
     * Create a new skill
     * @param skill the skill to create
     * @return the created skill
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if category not found
     * @throws com.company.internalmgmt.common.exception.ResourceAlreadyExistsException if skill name already exists in category
     */
    @PreAuthorize("hasAuthority('skill:create')")
    Skill createSkill(Skill skill);

    /**
     * Create a new skill from request
     * @param request the skill request to create
     * @return the created skill
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if category not found
     * @throws com.company.internalmgmt.common.exception.ResourceAlreadyExistsException if skill name already exists in category
     */
    @PreAuthorize("hasAuthority('skill:create')")
    Skill createSkill(SkillRequest request);

    /**
     * Update an existing skill
     * @param id the skill ID
     * @param skill the updated skill
     * @return the updated skill
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if not found
     * @throws com.company.internalmgmt.common.exception.ResourceAlreadyExistsException if skill name already exists in category
     */
    @PreAuthorize("hasAuthority('skill:update')")
    Skill updateSkill(Long id, Skill skill);

    /**
     * Update an existing skill from request
     * @param id the skill ID
     * @param request the updated skill request
     * @return the updated skill
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if not found
     * @throws com.company.internalmgmt.common.exception.ResourceAlreadyExistsException if skill name already exists in category
     */
    @PreAuthorize("hasAuthority('skill:update')")
    Skill updateSkill(Long id, SkillRequest request);

    /**
     * Delete a skill
     * @param id the skill ID
     * @throws com.company.internalmgmt.common.exception.ResourceNotFoundException if not found
     * @throws com.company.internalmgmt.common.exception.ResourceInUseException if skill is being used by employees
     */
    @PreAuthorize("hasAuthority('skill:delete')")
    void deleteSkill(Long id);

    /**
     * Get skills by IDs
     * @param ids list of skill IDs
     * @return List of skills
     */
    @PreAuthorize("hasAuthority('skill:read')")
    List<Skill> getSkillsByIds(List<Long> ids);

    /**
     * Find all skills with filtering options
     * 
     * @param categoryId filter by category ID
     * @param search search term
     * @param pageable pagination info
     * @return page of skill DTOs
     */
    Page<SkillDto> findAll(Long categoryId, String search, Pageable pageable);
    
    /**
     * Find all skills in a category
     * 
     * @param categoryId the category ID
     * @return list of skill DTOs
     */
    List<SkillDto> findByCategoryId(Long categoryId);
    
    /**
     * Find a skill by ID
     * 
     * @param id the skill ID
     * @return the skill DTO
     */
    SkillDto findById(Long id);
    
    /**
     * Find skills by IDs
     * 
     * @param ids list of skill IDs
     * @return list of skill DTOs
     */
    List<SkillDto> findByIds(List<Long> ids);
    
    /**
     * Create a new skill
     * 
     * @param skillDto the skill DTO
     * @return the created skill DTO
     */
    SkillDto create(SkillDto skillDto);
    
    /**
     * Update a skill
     * 
     * @param id the skill ID
     * @param skillDto the skill DTO
     * @return the updated skill DTO
     */
    SkillDto update(Long id, SkillDto skillDto);
    
    /**
     * Delete a skill
     * 
     * @param id the skill ID
     */
    void delete(Long id);
    
    /**
     * Search skills by name
     * 
     * @param searchTerm the search term
     * @param pageable pagination info
     * @return page of skill DTOs
     */
    Page<SkillDto> searchByName(String searchTerm, Pageable pageable);
    
    /**
     * Check if a skill exists with the given name in a category
     * 
     * @param name the skill name
     * @param categoryId the category ID
     * @return true if a skill with the given name exists in the specified category
     */
    boolean existsByNameAndCategoryId(String name, Long categoryId);
} 
