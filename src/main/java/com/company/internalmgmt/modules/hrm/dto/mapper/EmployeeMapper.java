package com.company.internalmgmt.modules.hrm.dto.mapper;

import java.time.LocalDateTime;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.internalmgmt.modules.admin.service.UserService;
import com.company.internalmgmt.modules.hrm.dto.EmployeeDto;
import com.company.internalmgmt.modules.hrm.dto.EmployeeRequest;
import com.company.internalmgmt.modules.hrm.dto.EmployeeResponse;
import com.company.internalmgmt.modules.hrm.model.Employee;
import com.company.internalmgmt.modules.hrm.repository.TeamRepository;

/**
 * Mapper for converting between Employee entity and DTOs
 */
@Service
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {UserService.class}
)
public abstract class EmployeeMapper {

    @Autowired
    protected TeamRepository teamRepository;

    /**
     * Maps an Employee entity to an EmployeeDto
     * 
     * @param employee the source entity
     * @return the mapped DTO
     */
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "reportingLeaderName", ignore = true)
    @Mapping(target = "team", expression = "java(mapTeamToTeamDto(employee))")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    public abstract EmployeeDto toDto(Employee employee);
    
    /**
     * Maps an EmployeeRequest to a new Employee entity
     * 
     * @param request the source request
     * @return the mapped entity
     */
    @Mapping(target = "team", ignore = true)
    public abstract Employee toEntity(EmployeeRequest request);
    
    /**
     * Updates an existing Employee entity with data from an EmployeeRequest
     * 
     * @param request the request containing updated data
     * @param employee the entity to update
     */
    @Mapping(target = "team", ignore = true)
    public abstract void updateEntityFromRequest(EmployeeRequest request, @MappingTarget Employee employee);

    /**
     * Maps an Employee entity to an EmployeeResponse
     * 
     * @param employee the source entity
     * @return the mapped response DTO
     */
    @Mapping(target = "fullName", expression = "java(employee.getFirstName() + \" \" + employee.getLastName())")
    @Mapping(target = "team", expression = "java(mapTeamToResponseTeamDto(employee))")
    @Mapping(target = "skills", ignore = true)
    @Mapping(target = "projectHistory", ignore = true)
    @Mapping(target = "statusLogs", ignore = true)
    public abstract EmployeeResponse toResponse(Employee employee);
    
    /**
     * Helper method to map Team to TeamDto
     */
    protected EmployeeDto.TeamDto mapTeamToTeamDto(Employee employee) {
        if (employee.getTeam() == null) {
            return null;
        }
        return EmployeeDto.TeamDto.builder()
                .id(employee.getTeam().getId())
                .name(employee.getTeam().getName())
                .build();
    }
    
    /**
     * Helper method to map Team to EmployeeResponse.TeamDto
     */
    protected EmployeeResponse.TeamDto mapTeamToResponseTeamDto(Employee employee) {
        if (employee.getTeam() == null) {
            return null;
        }
        return EmployeeResponse.TeamDto.builder()
                .id(employee.getTeam().getId())
                .name(employee.getTeam().getName())
                .build();
    }
    
    /**
     * After mapping from request to entity, set the team
     */
    @AfterMapping
    protected void afterMappingFromRequest(EmployeeRequest request, @MappingTarget Employee employee) {
        if (request.getTeamId() != null && teamRepository != null) {
            teamRepository.findById(request.getTeamId())
                .ifPresent(employee::setTeam);
        }
    }
} 
