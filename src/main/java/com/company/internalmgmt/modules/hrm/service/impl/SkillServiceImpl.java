package com.company.internalmgmt.modules.hrm.service.impl;

import com.company.internalmgmt.common.exception.ResourceAlreadyExistsException;
import com.company.internalmgmt.common.exception.ResourceInUseException;
import com.company.internalmgmt.common.exception.ResourceNotFoundException;
import com.company.internalmgmt.modules.hrm.dto.SkillDto;
import com.company.internalmgmt.modules.hrm.dto.SkillRequest;
import com.company.internalmgmt.modules.hrm.dto.mapper.SkillMapper;
import com.company.internalmgmt.modules.hrm.model.Skill;
import com.company.internalmgmt.modules.hrm.model.SkillCategory;
import com.company.internalmgmt.modules.hrm.repository.EmployeeSkillRepository;
import com.company.internalmgmt.modules.hrm.repository.SkillCategoryRepository;
import com.company.internalmgmt.modules.hrm.repository.SkillRepository;
import com.company.internalmgmt.modules.hrm.service.SkillService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of SkillService
 */
@Service
public class SkillServiceImpl implements SkillService {

    private final SkillRepository skillRepository;
    private final SkillCategoryRepository skillCategoryRepository;
    private final EmployeeSkillRepository employeeSkillRepository;
    private final SkillMapper skillMapper;

    public SkillServiceImpl(SkillRepository skillRepository, SkillCategoryRepository skillCategoryRepository, EmployeeSkillRepository employeeSkillRepository, SkillMapper skillMapper) {
        this.skillRepository = skillRepository;
        this.skillCategoryRepository = skillCategoryRepository;
        this.employeeSkillRepository = employeeSkillRepository;
        this.skillMapper = skillMapper;
    }

    @Override
    @PreAuthorize("hasAuthority('skill-category:read')")
    public List<SkillCategory> getAllCategories() {
        return skillCategoryRepository.findAll();
    }

    @Override
    @PreAuthorize("hasAuthority('skill-category:read')")
    public SkillCategory getCategoryById(Long id) {
        return skillCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill category not found with id: " + id));
    }

    @Override
    @PreAuthorize("hasAuthority('skill-category:create')")
    @Transactional
    public SkillCategory createCategory(SkillCategory category) {
        if (skillCategoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new ResourceAlreadyExistsException("Skill category already exists with name: " + category.getName());
        }
        
        return skillCategoryRepository.save(category);
    }

    @Override
    @PreAuthorize("hasAuthority('skill-category:update')")
    @Transactional
    public SkillCategory updateCategory(Long id, SkillCategory category) {
        SkillCategory existingCategory = getCategoryById(id);
        
        if (!existingCategory.getName().equals(category.getName()) && 
                skillCategoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new ResourceAlreadyExistsException("Skill category already exists with name: " + category.getName());
        }
        
        existingCategory.setName(category.getName());
        existingCategory.setDescription(category.getDescription());
        
        return skillCategoryRepository.save(existingCategory);
    }

    @Override
    @PreAuthorize("hasAuthority('skill-category:delete')")
    @Transactional
    public void deleteCategory(Long id) {
        SkillCategory category = getCategoryById(id);
        
        long skillCount = skillRepository.countByCategoryId(id);
        if (skillCount > 0) {
            throw new ResourceInUseException(
                    "Cannot delete skill category. It is being used by " + skillCount + " skills.");
        }
        
        skillCategoryRepository.delete(category);
    }

    @Override
    @PreAuthorize("hasAuthority('skill:read')")
    public List<Skill> getAllSkills(Long categoryId) {
        if (categoryId != null) {
            return skillRepository.findByCategoryId(categoryId);
        }
        
        return skillRepository.findAll();
    }

    @Override
    @PreAuthorize("hasAuthority('skill:read')")
    public List<Skill> getSkillsByCategoryId(Long categoryId) {
        if (!skillCategoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Skill category not found with id: " + categoryId);
        }
        
        return skillRepository.findByCategoryId(categoryId);
    }

    @Override
    @PreAuthorize("hasAuthority('skill:read')")
    public Page<Skill> getSkills(Long categoryId, String search, Pageable pageable) {
        if (categoryId != null && StringUtils.hasText(search)) {
            return skillRepository.findByCategoryIdAndNameContainingIgnoreCase(categoryId, search, pageable);
        } else if (categoryId != null) {
            return skillRepository.findByCategoryId(categoryId, pageable);
        } else if (StringUtils.hasText(search)) {
            return skillRepository.findByNameContainingIgnoreCase(search, pageable);
        } else {
            return skillRepository.findAll(pageable);
        }
    }

    @Override
    @PreAuthorize("hasAuthority('skill:read')")
    public Skill getSkillById(Long id) {
        return findSkillById(id);
    }

    @Override
    @PreAuthorize("hasAuthority('skill:create')")
    @Transactional
    public Skill createSkill(Skill skill) {
        if (skill.getCategory() == null || skill.getCategory().getId() == null) {
            throw new IllegalArgumentException("Skill category is required");
        }
        
        SkillCategory category = skillCategoryRepository.findById(skill.getCategory().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill category not found with id: " + skill.getCategory().getId()));
        
        if (skillRepository.existsByNameIgnoreCaseAndCategoryId(skill.getName(), category.getId())) {
            throw new ResourceAlreadyExistsException("Skill already exists with name: " + skill.getName() + " in the specified category");
        }
        
        skill.setCategory(category);
        return skillRepository.save(skill);
    }

    @Override
    @PreAuthorize("hasAuthority('skill:create')")
    @Transactional
    public Skill createSkill(SkillRequest request) {
        SkillCategory category = skillCategoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill category not found with id: " + request.getCategoryId()));
        
        if (skillRepository.existsByNameIgnoreCaseAndCategoryId(request.getName(), category.getId())) {
            throw new ResourceAlreadyExistsException("Skill already exists with name: " + request.getName() + " in the specified category");
        }
        
        Skill skill = new Skill();
        skill.setName(request.getName());
        skill.setDescription(request.getDescription());
        skill.setCategory(category);
        
        return skillRepository.save(skill);
    }

    @Override
    @PreAuthorize("hasAuthority('skill:update')")
    @Transactional
    public Skill updateSkill(Long id, Skill skill) {
        Skill existingSkill = findSkillById(id);
        
        if (skill.getCategory() != null && skill.getCategory().getId() != null && 
                !existingSkill.getCategory().getId().equals(skill.getCategory().getId())) {
            
            SkillCategory newCategory = skillCategoryRepository.findById(skill.getCategory().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Skill category not found with id: " + skill.getCategory().getId()));
            
            if (skillRepository.existsByNameIgnoreCaseAndCategoryId(skill.getName(), newCategory.getId())) {
                throw new ResourceAlreadyExistsException("Skill already exists with name: " + skill.getName() + " in the target category");
            }
            
            existingSkill.setCategory(newCategory);
        }
        else if (!existingSkill.getName().equals(skill.getName()) && 
                skillRepository.existsByNameIgnoreCaseAndCategoryId(skill.getName(), existingSkill.getCategory().getId())) {
            throw new ResourceAlreadyExistsException("Skill already exists with name: " + skill.getName() + " in this category");
        }
        
        existingSkill.setName(skill.getName());
        existingSkill.setDescription(skill.getDescription());
        
        return skillRepository.save(existingSkill);
    }

    @Override
    @PreAuthorize("hasAuthority('skill:update')")
    @Transactional
    public Skill updateSkill(Long id, SkillRequest request) {
        Skill existingSkill = findSkillById(id);
        
        if (request.getCategoryId() != null && !existingSkill.getCategory().getId().equals(request.getCategoryId())) {
            
            SkillCategory newCategory = skillCategoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Skill category not found with id: " + request.getCategoryId()));
            
            if (skillRepository.existsByNameIgnoreCaseAndCategoryId(request.getName(), newCategory.getId())) {
                throw new ResourceAlreadyExistsException("Skill already exists with name: " + request.getName() + " in the target category");
            }
            
            existingSkill.setCategory(newCategory);
        }
        else if (!existingSkill.getName().equals(request.getName()) && 
                skillRepository.existsByNameIgnoreCaseAndCategoryId(request.getName(), existingSkill.getCategory().getId())) {
            throw new ResourceAlreadyExistsException("Skill already exists with name: " + request.getName() + " in this category");
        }
        
        existingSkill.setName(request.getName());
        existingSkill.setDescription(request.getDescription());
        
        return skillRepository.save(existingSkill);
    }

    @Override
    @PreAuthorize("hasAuthority('skill:delete')")
    @Transactional
    public void deleteSkill(Long id) {
        Skill skill = findSkillById(id);
        
        long employeeCount = employeeSkillRepository.countBySkillId(id);
        if (employeeCount > 0) {
            throw new ResourceInUseException(
                    "Cannot delete skill. It is being used by " + employeeCount + " employees.");
        }
        
        skillRepository.delete(skill);
    }

    @Override
    @PreAuthorize("hasAuthority('skill:read')")
    public List<Skill> getSkillsByIds(List<Long> ids) {
        return skillRepository.findAllById(ids);
    }

    @Override
    public Page<SkillDto> findAll(Long categoryId, String search, Pageable pageable) {
        Page<Skill> skillPage = getSkills(categoryId, search, pageable);
        return skillPage.map(skillMapper::toDto);
    }

    @Override
    public List<SkillDto> findByCategoryId(Long categoryId) {
        List<Skill> skills = getSkillsByCategoryId(categoryId);
        return skills.stream()
                .map(skillMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public SkillDto findById(Long id) {
        Skill skill = findSkillById(id);
        return skillMapper.toDto(skill);
    }

    @Override
    public List<SkillDto> findByIds(List<Long> ids) {
        List<Skill> skills = getSkillsByIds(ids);
        return skills.stream()
                .map(skillMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SkillDto create(SkillDto skillDto) {
        SkillCategory category = skillCategoryRepository.findById(skillDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Skill category not found with id: " + skillDto.getCategoryId()));
        
        if (skillRepository.existsByNameIgnoreCaseAndCategoryId(skillDto.getName(), category.getId())) {
            throw new ResourceAlreadyExistsException("Skill already exists with name: " + skillDto.getName() + " in the specified category");
        }
        
        Skill skill = new Skill();
        skill.setName(skillDto.getName());
        skill.setDescription(skillDto.getDescription());
        skill.setCategory(category);
        
        skill = skillRepository.save(skill);
        return skillMapper.toDto(skill);
    }

    @Override
    @Transactional
    public SkillDto update(Long id, SkillDto skillDto) {
        Skill existingSkill = findSkillById(id);
        
        if (!existingSkill.getCategory().getId().equals(skillDto.getCategoryId())) {
            
            SkillCategory newCategory = skillCategoryRepository.findById(skillDto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Skill category not found with id: " + skillDto.getCategoryId()));
            
            if (skillRepository.existsByNameIgnoreCaseAndCategoryId(skillDto.getName(), newCategory.getId())) {
                throw new ResourceAlreadyExistsException("Skill already exists with name: " + skillDto.getName() + " in the target category");
            }
            
            existingSkill.setCategory(newCategory);
        }
        else if (!existingSkill.getName().equals(skillDto.getName()) && 
                skillRepository.existsByNameIgnoreCaseAndCategoryId(skillDto.getName(), existingSkill.getCategory().getId())) {
            throw new ResourceAlreadyExistsException("Skill already exists with name: " + skillDto.getName() + " in this category");
        }
        
        existingSkill.setName(skillDto.getName());
        existingSkill.setDescription(skillDto.getDescription());
        
        existingSkill = skillRepository.save(existingSkill);
        return skillMapper.toDto(existingSkill);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        deleteSkill(id);
    }

    @Override
    public Page<SkillDto> searchByName(String searchTerm, Pageable pageable) {
        Page<Skill> skillPage = skillRepository.findByNameContainingIgnoreCase(searchTerm, pageable);
        return skillPage.map(skillMapper::toDto);
    }

    @Override
    public boolean existsByNameAndCategoryId(String name, Long categoryId) {
        if (categoryId == null) {
            return false;
        }
        
        return skillCategoryRepository.findById(categoryId)
                .map(category -> skillRepository.existsByNameIgnoreCaseAndCategoryId(name, category.getId()))
                .orElse(false);
    }
    
    private Skill findSkillById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));
    }
} 
