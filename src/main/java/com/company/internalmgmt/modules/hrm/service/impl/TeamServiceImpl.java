package com.company.internalmgmt.modules.hrm.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.internalmgmt.common.exception.ResourceNotFoundException;
import com.company.internalmgmt.modules.hrm.dto.TeamDto;
import com.company.internalmgmt.modules.hrm.dto.TeamRequest;
import com.company.internalmgmt.modules.hrm.dto.mapper.TeamMapper;
import com.company.internalmgmt.modules.hrm.model.Employee;
import com.company.internalmgmt.modules.hrm.model.Team;
import com.company.internalmgmt.modules.hrm.repository.EmployeeRepository;
import com.company.internalmgmt.modules.hrm.repository.TeamRepository;
import com.company.internalmgmt.modules.hrm.service.TeamService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of the TeamService
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final EmployeeRepository employeeRepository;
    private final TeamMapper teamMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TeamDto createTeam(TeamRequest request) {
        Team team = teamMapper.toEntity(request);
        Team savedTeam = teamRepository.save(team);
        log.info("Created team with ID: {}", savedTeam.getId());
        return teamMapper.toDto(savedTeam);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public TeamDto updateTeam(Long id, TeamRequest request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id " + id));
        
        teamMapper.updateEntityFromRequest(request, team);
        Team updatedTeam = teamRepository.save(team);
        log.info("Updated team with ID: {}", id);
        return teamMapper.toDto(updatedTeam);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public TeamDto getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id " + id));
        return teamMapper.toDtoWithMembers(team, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<TeamDto> getAllTeams() {
        List<Team> teams = teamRepository.findAllActive();
        return teams.stream()
                .map(team -> teamMapper.toDto(team))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TeamDto> getAllTeams(Pageable pageable) {
        Page<Team> teamPage = teamRepository.findAll(pageable);
        List<TeamDto> teamDtos = teamPage.getContent().stream()
                .map(teamMapper::toDto)
                .collect(Collectors.toList());
        return new PageImpl<>(teamDtos, pageable, teamPage.getTotalElements());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id " + id));
        // Implement soft delete if needed
        // team.setDeletedAt(LocalDateTime.now());
        // teamRepository.save(team);
        
        // Or hard delete
        teamRepository.delete(team);
        log.info("Deleted team with ID: {}", id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<TeamDto> searchTeamsByName(String name) {
        List<Team> teams = teamRepository.searchByName(name);
        return teams.stream()
                .map(teamMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<TeamDto> getTeamsByLeaderId(Long leaderId) {
        List<Team> teams = teamRepository.findByLeader_Id(leaderId);
        return teams.stream()
                .map(teamMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Long> getTeamMemberIds(Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id " + teamId));
        
        // Find employees in this team using new relationship
        return team.getEmployees().stream()
                .map(Employee::getId)
                .collect(Collectors.toList());
    }
} 