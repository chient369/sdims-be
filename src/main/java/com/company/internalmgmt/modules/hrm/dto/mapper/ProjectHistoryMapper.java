package com.company.internalmgmt.modules.hrm.dto.mapper;

import java.time.LocalDateTime;

import org.mapstruct.*;
import org.springframework.stereotype.Service;

import com.company.internalmgmt.modules.hrm.dto.ProjectHistoryDto;
import com.company.internalmgmt.modules.hrm.dto.ProjectHistoryRequest;
import com.company.internalmgmt.modules.hrm.model.Employee;
import com.company.internalmgmt.modules.hrm.model.ProjectHistory;

/**
 * Mapper for converting between ProjectHistory entity and DTOs
 */
@Service
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ProjectHistoryMapper {

    /**
     * Maps a ProjectHistory entity to a ProjectHistoryDto
     * 
     * @param projectHistory the source entity
     * @return the mapped DTO
     */
    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeName", expression = "java(projectHistory.getEmployee().getFirstName() + \" \" + projectHistory.getEmployee().getLastName())")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    ProjectHistoryDto toDto(ProjectHistory projectHistory);
    
    /**
     * Maps a ProjectHistoryRequest to a new ProjectHistory entity
     * 
     * @param request the source request
     * @return the mapped entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProjectHistory toEntity(ProjectHistoryRequest request);
    
    /**
     * Updates an existing ProjectHistory entity with data from a ProjectHistoryRequest
     * 
     * @param request the request containing updated data
     * @param projectHistory the entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(ProjectHistoryRequest request, @MappingTarget ProjectHistory projectHistory);

    default ProjectHistory fromRequestWithEmployee(ProjectHistoryRequest request, Employee employee) {
        ProjectHistory projectHistory = toEntity(request);
        projectHistory.setEmployee(employee);
        return projectHistory;
    }
} 
