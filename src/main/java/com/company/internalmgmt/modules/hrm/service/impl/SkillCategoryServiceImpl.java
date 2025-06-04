package com.company.internalmgmt.modules.hrm.service.impl;

import com.company.internalmgmt.common.exception.ResourceAlreadyExistsException;
import com.company.internalmgmt.common.exception.ResourceInUseException;
import com.company.internalmgmt.common.exception.ResourceNotFoundException;
import com.company.internalmgmt.modules.hrm.dto.SkillCategoryDto;
import com.company.internalmgmt.modules.hrm.dto.SkillCategoryRequest;
import com.company.internalmgmt.modules.hrm.dto.mapper.SkillCategoryMapper;
import com.company.internalmgmt.modules.hrm.model.SkillCategory;
import com.company.internalmgmt.modules.hrm.repository.SkillCategoryRepository;
import com.company.internalmgmt.modules.hrm.repository.SkillRepository;
import com.company.internalmgmt.modules.hrm.service.SkillCategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of SkillCategoryService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SkillCategoryServiceImpl implements SkillCategoryService {

    private final SkillCategoryRepository skillCategoryRepository;
    private final SkillRepository skillRepository;
    private final SkillCategoryMapper skillCategoryMapper;

    @Override
    public List<SkillCategoryDto> findAll() {
        log.debug("Request to get all SkillCategories");
        List<SkillCategory> categories = skillCategoryRepository.findAll();
        return categories.stream()
                .map(skillCategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public SkillCategoryDto findById(Long id) {
        log.debug("Request to get SkillCategory by id: {}", id);
        SkillCategory category = findCategoryById(id);
        return skillCategoryMapper.toDto(category);
    }

    @Override
    public SkillCategoryDto findByName(String name) {
        log.debug("Request to get SkillCategory by name: {}", name);
        return skillCategoryRepository.findByNameIgnoreCase(name)
                .map(skillCategoryMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Skill category not found with name: " + name));
    }

    @Override
    @PreAuthorize("hasAuthority('skill-category:create')")
    @Transactional
    public SkillCategoryDto create(SkillCategoryRequest categoryRequest) {
        log.debug("Request to create SkillCategory: {}", categoryRequest);
        
        if (existsByName(categoryRequest.getName())) {
            throw new ResourceAlreadyExistsException("Skill category already exists with name: " + categoryRequest.getName());
        }
        
        SkillCategory category = skillCategoryMapper.toEntity(categoryRequest);
        category = skillCategoryRepository.save(category);
        return skillCategoryMapper.toDto(category);
    }

    @Override
    @PreAuthorize("hasAuthority('skill-category:update')")
    @Transactional
    public SkillCategoryDto update(Long id, SkillCategoryRequest categoryRequest) {
        log.debug("Request to update SkillCategory: {}", categoryRequest);
        
        SkillCategory existingCategory = findCategoryById(id);
        
        // Check if new name conflicts with existing one (not the same category)
        if (!existingCategory.getName().equals(categoryRequest.getName()) && 
                existsByName(categoryRequest.getName())) {
            throw new ResourceAlreadyExistsException("Skill category already exists with name: " + categoryRequest.getName());
        }
        
        // Update fields
        existingCategory.setName(categoryRequest.getName());
        existingCategory.setDescription(categoryRequest.getDescription());
        
        existingCategory = skillCategoryRepository.save(existingCategory);
        return skillCategoryMapper.toDto(existingCategory);
    }

    @Override
    @PreAuthorize("hasAuthority('skill-category:delete')")
    @Transactional
    public void delete(Long id) {
        log.debug("Request to delete SkillCategory: {}", id);
        
        SkillCategory category = findCategoryById(id);
        
        // Check if any skills are using this category
        long skillCount = skillRepository.countByCategoryId(category.getId());
        if (skillCount > 0) {
            throw new ResourceInUseException(
                    "Cannot delete skill category. It is being used by " + skillCount + " skills.");
        }
        
        skillCategoryRepository.delete(category);
    }

    @Override
    public boolean existsByName(String name) {
        return skillCategoryRepository.existsByNameIgnoreCase(name);
    }
    
    /**
     * Helper method to find a category by ID
     * 
     * @param id the category ID
     * @return the skill category
     * @throws ResourceNotFoundException if not found
     */
    private SkillCategory findCategoryById(Long id) {
        return skillCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill category not found with id: " + id));
    }
} 