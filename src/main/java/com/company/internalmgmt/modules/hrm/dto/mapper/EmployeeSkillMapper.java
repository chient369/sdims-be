package com.company.internalmgmt.modules.hrm.dto.mapper;

import java.time.LocalDateTime;

import org.mapstruct.*;
import org.springframework.stereotype.Service;

import com.company.internalmgmt.modules.hrm.dto.EmployeeSkillDto;
import com.company.internalmgmt.modules.hrm.dto.EmployeeSkillRequest;
import com.company.internalmgmt.modules.hrm.model.Employee;
import com.company.internalmgmt.modules.hrm.model.EmployeeSkill;
import com.company.internalmgmt.modules.hrm.model.Skill;

/**
 * Mapper for EmployeeSkill entity and DTOs
 */
@Service
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface EmployeeSkillMapper {

    /**
     * Maps an EmployeeSkill entity to an EmployeeSkillDto
     * 
     * @param employeeSkill the source entity
     * @return the mapped DTO
     */
    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "skillId", source = "skill.id")
    @Mapping(target = "skillName", source = "skill.name")
    @Mapping(target = "skillCategoryName", source = "skill.category.name")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    EmployeeSkillDto toDto(EmployeeSkill employeeSkill);

    /**
     * Maps an EmployeeSkillRequest to a new EmployeeSkill entity
     * 
     * @param request the source request
     * @return the mapped entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "skill", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    EmployeeSkill toEntity(EmployeeSkillRequest request);

    /**
     * Updates an existing EmployeeSkill entity with data from an EmployeeSkillRequest
     * 
     * @param request the request containing updated data
     * @param employeeSkill the entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "skill", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(EmployeeSkillRequest request, @MappingTarget EmployeeSkill employeeSkill);

    @AfterMapping
    default void setNames(@MappingTarget EmployeeSkillDto employeeSkillDto, EmployeeSkill employeeSkill) {
        if (employeeSkill.getSkill() != null) {
            employeeSkillDto.setSkillName(employeeSkill.getSkill().getName());
            
            if (employeeSkill.getSkill().getCategory() != null) {
                employeeSkillDto.setSkillCategoryName(employeeSkill.getSkill().getCategory().getName());
            }
        }
    }

    default EmployeeSkill fromRequestWithEntities(EmployeeSkillRequest request, Employee employee, Skill skill) {
        EmployeeSkill employeeSkill = toEntity(request);
        employeeSkill.setEmployee(employee);
        employeeSkill.setSkill(skill);
        return employeeSkill;
    }
} 
