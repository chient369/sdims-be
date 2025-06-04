package com.company.internalmgmt.modules.hrm.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.company.internalmgmt.modules.hrm.dto.TeamDto;
import com.company.internalmgmt.modules.hrm.dto.TeamRequest;

/**
 * Service interface for managing teams
 */
public interface TeamService {
    
    /**
     * Create a new team
     * 
     * @param request the team creation request
     * @return the created team DTO
     */
    TeamDto createTeam(TeamRequest request);
    
    /**
     * Update an existing team
     * 
     * @param id the team ID
     * @param request the team update request
     * @return the updated team DTO
     */
    TeamDto updateTeam(Long id, TeamRequest request);
    
    /**
     * Get a team by ID
     * 
     * @param id the team ID
     * @return the team DTO
     */
    TeamDto getTeamById(Long id);
    
    /**
     * Get all teams
     * 
     * @return list of all teams
     */
    List<TeamDto> getAllTeams();
    
    /**
     * Get all teams with pagination
     * 
     * @param pageable pagination information
     * @return page of team DTOs
     */
    Page<TeamDto> getAllTeams(Pageable pageable);
    
    /**
     * Delete a team
     * 
     * @param id the team ID
     */
    void deleteTeam(Long id);
    
    /**
     * Search teams by name
     * 
     * @param name the name to search for
     * @return list of matching teams
     */
    List<TeamDto> searchTeamsByName(String name);
    
    /**
     * Get teams by leader ID
     * 
     * @param leaderId the leader ID
     * @return list of teams managed by the leader
     */
    List<TeamDto> getTeamsByLeaderId(Long leaderId);
    
    /**
     * Get team members
     * 
     * @param teamId the team ID
     * @return list of employees in the team
     */
    List<Long> getTeamMemberIds(Long teamId);
} 