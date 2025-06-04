package com.company.internalmgmt.modules.hrm.dto.mapper;

import java.time.LocalDateTime;

import org.mapstruct.*;
import org.springframework.stereotype.Service;

import com.company.internalmgmt.modules.hrm.dto.SkillDto;
import com.company.internalmgmt.modules.hrm.dto.SkillRequest;
import com.company.internalmgmt.modules.hrm.model.Skill;
import com.company.internalmgmt.modules.hrm.model.SkillCategory;

/**
 * Mapper for converting between Skill entity and DTOs
 */
@Service
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface SkillMapper {

    /**
     * Maps a Skill entity to a SkillDto
     * 
     * @param skill the source entity
     * @return the mapped DTO
     */
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    SkillDto toDto(Skill skill);
    
    /**
     * Maps a SkillRequest to a new Skill entity
     * 
     * @param request the source request
     * @return the mapped entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "employeeSkills", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Skill toEntity(SkillRequest request);
    
    /**
     * Updates an existing Skill entity with data from a SkillRequest
     * 
     * @param request the request containing updated data
     * @param skill the entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "employeeSkills", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(SkillRequest request, @MappingTarget Skill skill);

    default Skill fromSkillRequestWithCategory(SkillRequest skillRequest, SkillCategory category) {
        Skill skill = toEntity(skillRequest);
        skill.setCategory(category);
        return skill;
    }
} 
