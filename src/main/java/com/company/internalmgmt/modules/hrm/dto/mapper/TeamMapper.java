package com.company.internalmgmt.modules.hrm.dto.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.company.internalmgmt.modules.hrm.dto.EmployeeDto;
import com.company.internalmgmt.modules.hrm.dto.TeamDto;
import com.company.internalmgmt.modules.hrm.dto.TeamRequest;
import com.company.internalmgmt.modules.hrm.model.Employee;
import com.company.internalmgmt.modules.hrm.model.Team;
import com.company.internalmgmt.modules.hrm.repository.EmployeeRepository;

/**
 * Mapper for converting between Team entity and DTOs
 */
@Service
@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    imports = {LocalDateTime.class}
)
public abstract class TeamMapper {

    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * Maps a Team entity to a TeamDto
     * 
     * @param team the source entity
     * @return the mapped DTO
     */
    @Mapping(target = "leaderId", source = "leader.id")
    @Mapping(target = "leaderName", expression = "java(team.getLeader() != null ? team.getLeader().getFirstName() + \" \" + team.getLeader().getLastName() : null)")
    @Mapping(target = "parentTeamId", source = "parentTeam.id")
    @Mapping(target = "parentTeamName", source = "parentTeam.name")
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    @Mapping(target = "createdBy", source = "createdBy", qualifiedByName = "getUsernameById")
    @Mapping(target = "updatedBy", source = "updatedBy", qualifiedByName = "getUsernameById")
    public abstract TeamDto toDto(Team team);
    
    /**
     * Maps a TeamRequest to a new Team entity
     * 
     * @param request the source request
     * @return the mapped entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "leader", ignore = true)
    @Mapping(target = "parentTeam", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    public abstract Team toEntity(TeamRequest request);
    
    /**
     * Updates an existing Team entity with data from a TeamRequest
     * 
     * @param request the request containing updated data
     * @param team the entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "leader", ignore = true)
    @Mapping(target = "parentTeam", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    public abstract void updateEntityFromRequest(TeamRequest request, @MappingTarget Team team);
    
    /**
     * Enhances a TeamDto with additional data after mapping
     */
    @AfterMapping
    protected void enhanceTeamDto(Team team, @MappingTarget TeamDto dto) {
        // Set leader name
        if (team.getLeaderId() != null) {
            employeeRepository.findById(team.getLeaderId()).ifPresent(leader -> {
                dto.setLeaderName(leader.getFirstName() + " " + leader.getLastName());
            });
        }
        
        // Set parent team name
        if (team.getParentTeamId() != null) {
            // This would typically involve a TeamRepository, but we'll assume
            // it can be loaded from elsewhere (to avoid circular dependencies)
            // teamRepository.findById(team.getParentTeamId()).ifPresent(parent -> {
            //     dto.setParentTeamName(parent.getName());
            // });
        }
    }
    
    /**
     * Adds team members to the DTO if requested
     * 
     * @param team the team entity
     * @param includeMembers whether to include team members
     * @return the enhanced DTO
     */
    public TeamDto toDtoWithMembers(Team team, boolean includeMembers) {
        TeamDto dto = toDto(team);
        
        if (includeMembers) {
            // Use the direct relationship instead of querying by name
            List<EmployeeDto> memberDtos = team.getEmployees().stream()
                    .map(employeeMapper::toDto)
                    .collect(Collectors.toList());
            
            dto.setMembers(memberDtos);
        }
        
        return dto;
    }
    
    /**
     * Method to get username by user ID
     */
    @Named("getUsernameById")
    protected String getUsernameById(Long userId) {
        // This would typically involve a UserRepository, but we'll assume
        // it can be loaded from elsewhere or handled by a utility method
        return userId != null ? "User " + userId : null;
    }
    
    /**
     * Convert list of Team entities to list of TeamDtos
     *
     * @param teams the list of team entities
     * @return the list of team DTOs
     */
    public abstract List<TeamDto> toDtoList(List<Team> teams);
} 