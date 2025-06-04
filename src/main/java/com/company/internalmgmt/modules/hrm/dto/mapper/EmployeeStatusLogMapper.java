package com.company.internalmgmt.modules.hrm.dto.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import com.company.internalmgmt.modules.hrm.dto.EmployeeStatusLogDto;
import com.company.internalmgmt.modules.hrm.dto.StatusUpdateRequest;
import com.company.internalmgmt.modules.hrm.model.Employee;
import com.company.internalmgmt.modules.hrm.model.EmployeeStatusLog;
import com.company.internalmgmt.modules.hrm.repository.EmployeeRepository;

/**
 * Mapper for EmployeeStatusLog entity and DTOs
 */
@Mapper(
    componentModel = "spring",
    imports = {LocalDateTime.class}
)
public abstract class EmployeeStatusLogMapper {

    @Autowired
    private EmployeeRepository employeeRepository;
    
    /**
     * Convert EmployeeStatusLog entity to DTO
     *
     * @param employeeStatusLog the entity
     * @return the DTO
     */
    @Mapping(target = "employeeId", source = "employee.id")
    @Mapping(target = "employeeName", expression = "java(employeeStatusLog.getEmployee() != null ? employeeStatusLog.getEmployee().getFirstName() + \" \" + employeeStatusLog.getEmployee().getLastName() : null)")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    public abstract EmployeeStatusLogDto toDto(EmployeeStatusLog employeeStatusLog);
    
    /**
     * Convert StatusUpdateRequest to EmployeeStatusLog entity
     *
     * @param request the request
     * @param employee the employee
     * @return the entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", source = "employee")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    public abstract EmployeeStatusLog toEntity(StatusUpdateRequest request, Employee employee);
    
    /**
     * Convert StatusUpdateRequest to EmployeeStatusLog entity
     *
     * @param request the status update request
     * @return the employee status log entity
     */
    public EmployeeStatusLog fromStatusUpdateRequest(StatusUpdateRequest request) {
        EmployeeStatusLog statusLog = new EmployeeStatusLog();
        statusLog.setStatus(request.getStatus());
        statusLog.setNote(request.getNote());
        statusLog.setLogTimestamp(LocalDateTime.now());
        statusLog.setExpectedEndDate(request.getExpectedEndDate());
        statusLog.setProjectName(request.getProjectName());
        return statusLog;
    }
    
    /**
     * Update EmployeeStatusLog from DTO
     *
     * @param dto the DTO
     * @param entity the entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employee", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    public abstract void updateFromDto(EmployeeStatusLogDto dto, @MappingTarget EmployeeStatusLog entity);
    
    /**
     * Convert list of entities to list of DTOs
     *
     * @param logs the list of entities
     * @return the list of DTOs
     */
    public abstract List<EmployeeStatusLogDto> toDtoList(List<EmployeeStatusLog> logs);
} 
