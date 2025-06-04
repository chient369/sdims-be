package com.company.internalmgmt.modules.hrm.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.company.internalmgmt.common.dto.PageResponseDto;
import com.company.internalmgmt.modules.hrm.dto.TeamDto;
import com.company.internalmgmt.modules.hrm.dto.TeamRequest;
import com.company.internalmgmt.modules.hrm.service.TeamService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for managing teams
 */
@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Team", description = "API for managing teams")
public class TeamController {

    private final TeamService teamService;

    /**
     * GET /api/v1/teams : Get all teams
     *
     * @param pageable pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of teams in body
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('employee:read:all', 'employee:read:team')")
    @Operation(summary = "Get all teams", description = "Returns a paginated list of teams")
    public ResponseEntity<PageResponseDto<TeamDto>> getAllTeams(Pageable pageable) {
        log.debug("REST request to get all Teams");
        Page<TeamDto> page = teamService.getAllTeams(pageable);
        PageResponseDto<TeamDto> response = PageResponseDto.success(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getSort().toString()
        );
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/teams/{id} : Get team by id
     *
     * @param id the id of the team to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the team, or with status 404 (Not Found)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('employee:read:all', 'employee:read:team')")
    @Operation(summary = "Get team by id", description = "Returns a team based on id")
    public ResponseEntity<TeamDto> getTeam(
            @Parameter(description = "ID of the team", required = true)
            @PathVariable Long id) {
        log.debug("REST request to get Team : {}", id);
        TeamDto team = teamService.getTeamById(id);
        return ResponseEntity.ok(team);
    }

    /**
     * POST /api/v1/teams : Create a new team
     *
     * @param request the team to create
     * @return the ResponseEntity with status 201 (Created) and with body the new team
     */
    @PostMapping
    @PreAuthorize("hasAuthority('employee:create')")
    @Operation(summary = "Create a new team", description = "Creates a new team and returns it")
    public ResponseEntity<TeamDto> createTeam(
            @Parameter(description = "Team to create", required = true)
            @Valid @RequestBody TeamRequest request) {
        log.debug("REST request to create Team : {}", request);
        TeamDto result = teamService.createTeam(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    /**
     * PUT /api/v1/teams/{id} : Update an existing team
     *
     * @param id the id of the team to update
     * @param request the team to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated team
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('employee:update:all')")
    @Operation(summary = "Update an existing team", description = "Updates a team based on id and returns it")
    public ResponseEntity<TeamDto> updateTeam(
            @Parameter(description = "ID of the team", required = true)
            @PathVariable Long id,
            @Parameter(description = "Team data to update", required = true)
            @Valid @RequestBody TeamRequest request) {
        log.debug("REST request to update Team : {}", id);
        TeamDto result = teamService.updateTeam(id, request);
        return ResponseEntity.ok(result);
    }

    /**
     * DELETE /api/v1/teams/{id} : Delete a team
     *
     * @param id the id of the team to delete
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('employee:delete')")
    @Operation(summary = "Delete a team", description = "Deletes a team based on id")
    public ResponseEntity<Void> deleteTeam(
            @Parameter(description = "ID of the team", required = true)
            @PathVariable Long id) {
        log.debug("REST request to delete Team : {}", id);
        teamService.deleteTeam(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/teams/search : Search teams by name
     *
     * @param name the name to search for
     * @return the ResponseEntity with status 200 (OK) and the list of teams in body
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('employee:read:all', 'employee:read:team')")
    @Operation(summary = "Search teams by name", description = "Returns teams with names containing the search term")
    public ResponseEntity<List<TeamDto>> searchTeams(
            @Parameter(description = "Name to search for", required = true)
            @RequestParam String name) {
        log.debug("REST request to search Teams for name : {}", name);
        List<TeamDto> teams = teamService.searchTeamsByName(name);
        return ResponseEntity.ok(teams);
    }

    /**
     * GET /api/v1/teams/leader/{leaderId} : Get teams by leader
     *
     * @param leaderId the ID of the leader
     * @return the ResponseEntity with status 200 (OK) and the list of teams in body
     */
    @GetMapping("/leader/{leaderId}")
    @PreAuthorize("hasAnyAuthority('employee:read:all', 'employee:read:team')")
    @Operation(summary = "Get teams by leader", description = "Returns teams led by the specified leader")
    public ResponseEntity<List<TeamDto>> getTeamsByLeader(
            @Parameter(description = "ID of the leader", required = true)
            @PathVariable Long leaderId) {
        log.debug("REST request to get Teams for leader : {}", leaderId);
        List<TeamDto> teams = teamService.getTeamsByLeaderId(leaderId);
        return ResponseEntity.ok(teams);
    }

    /**
     * GET /api/v1/teams/{id}/members : Get team members
     *
     * @param id the ID of the team
     * @return the ResponseEntity with status 200 (OK) and the list of member IDs in body
     */
    @GetMapping("/{id}/members")
    @PreAuthorize("hasAnyAuthority('employee:read:all', 'employee:read:team')")
    @Operation(summary = "Get team members", description = "Returns IDs of employees in the specified team")
    public ResponseEntity<List<Long>> getTeamMembers(
            @Parameter(description = "ID of the team", required = true)
            @PathVariable Long id) {
        log.debug("REST request to get members for Team : {}", id);
        List<Long> memberIds = teamService.getTeamMemberIds(id);
        return ResponseEntity.ok(memberIds);
    }
} 