package com.company.internalmgmt.modules.hrm.dto.mapper;

import com.company.internalmgmt.modules.hrm.dto.SkillCategoryDto;
import com.company.internalmgmt.modules.hrm.dto.SkillCategoryRequest;
import com.company.internalmgmt.modules.hrm.model.SkillCategory;
import org.mapstruct.*;
import org.springframework.stereotype.Service;

/**
 * Mapper for SkillCategory entity and DTOs
 */
@Mapper(componentModel = "spring", uses = {SkillMapper.class})
@Service
public interface SkillCategoryMapper {

    @Mapping(target = "skills", ignore = true)
    SkillCategoryDto toDto(SkillCategory skillCategory);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "skills", ignore = true)
    SkillCategory toEntity(SkillCategoryRequest skillCategoryRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "skills", ignore = true)
    void updateSkillCategoryFromDto(SkillCategoryRequest skillCategoryRequest, @MappingTarget SkillCategory skillCategory);
} 
