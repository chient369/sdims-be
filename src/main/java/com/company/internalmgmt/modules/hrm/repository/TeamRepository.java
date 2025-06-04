package com.company.internalmgmt.modules.hrm.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.company.internalmgmt.modules.hrm.model.Team;

/**
 * Repository for managing Team entities
 */
@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    /**
     * Find team by name
     *
     * @param name the team name
     * @return the team with the specified name
     */
    Team findByName(String name);

    /**
     * Find teams by department
     *
     * @param department the department
     * @return list of teams in the specified department
     */
    List<Team> findByDepartment(String department);

    /**
     * Find teams by leader ID
     *
     * @param leaderId the leader ID
     * @return list of teams led by the specified leader
     */
    List<Team> findByLeader_Id(Long leaderId);

    /**
     * Find teams by parent team ID
     *
     * @param parentTeamId the parent team ID
     * @return list of child teams
     */
    List<Team> findByParentTeam_Id(Long parentTeamId);

    /**
     * Search teams by name containing the search term
     *
     * @param searchTerm the search term
     * @return list of teams with names containing the search term
     */
    @Query("SELECT t FROM Team t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND t.deletedAt IS NULL")
    List<Team> searchByName(@Param("searchTerm") String searchTerm);

    /**
     * Find all non-deleted teams
     *
     * @return list of all active teams
     */
    @Query("SELECT t FROM Team t WHERE t.deletedAt IS NULL")
    List<Team> findAllActive();
} 