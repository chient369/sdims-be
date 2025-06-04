package com.company.internalmgmt.modules.hrm.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.company.internalmgmt.modules.hrm.model.EmployeeSkill;

/**
 * Repository for managing EmployeeSkill entities
 */
@Repository
public interface EmployeeSkillRepository extends JpaRepository<EmployeeSkill, Long> {

    /**
     * Find all skills for a specific employee
     *
     * @param employeeId the employee ID
     * @return list of employee skills
     */
    List<EmployeeSkill> findByEmployeeId(Long employeeId);

    /**
     * Find all skills for a specific employee with pagination
     *
     * @param employeeId the employee ID
     * @param pageable pagination information
     * @return page of employee skills
     */
    Page<EmployeeSkill> findByEmployeeId(Long employeeId, Pageable pageable);

    /**
     * Find a specific skill for an employee
     *
     * @param employeeId the employee ID
     * @param skillId the skill ID
     * @return the employee skill if found
     */
    Optional<EmployeeSkill> findByEmployeeIdAndSkillId(Long employeeId, Long skillId);

    /**
     * Check if an employee has a specific skill
     *
     * @param employeeId the employee ID
     * @param skillId the skill ID
     * @return true if the employee has the specified skill
     */
    boolean existsByEmployeeIdAndSkillId(Long employeeId, Long skillId);

    /**
     * Delete a specific skill for an employee
     *
     * @param employeeId the employee ID
     * @param skillId the skill ID
     */
    void deleteByEmployeeIdAndSkillId(Long employeeId, Long skillId);

    /**
     * Find employees with specific skills
     *
     * @param skillIds list of skill IDs
     * @return list of employee skills for employees having the specified skills
     */
    @Query("SELECT es FROM EmployeeSkill es WHERE es.skill.id IN :skillIds")
    List<EmployeeSkill> findBySkillIdIn(@Param("skillIds") List<Long> skillIds);

    /**
     * Find employees with specific skills and minimum years of experience
     *
     * @param skillIds list of skill IDs
     * @param minYearsExperience minimum years of experience
     * @return list of employee skills matching the criteria
     */
    @Query("SELECT es FROM EmployeeSkill es WHERE es.skill.id IN :skillIds AND es.yearsExperience >= :minYearsExperience")
    List<EmployeeSkill> findBySkillIdInAndMinExperience(
            @Param("skillIds") List<Long> skillIds,
            @Param("minYearsExperience") Double minYearsExperience);
            
    /**
     * Find employees with specific skills and minimum years of experience
     *
     * @param skillIds list of skill IDs
     * @param minYearsExperience minimum years of experience
     * @return list of employee skills matching the criteria
     */
    @Query("SELECT es FROM EmployeeSkill es WHERE es.skill.id IN :skillIds AND es.yearsExperience >= :minYearsExperience")
    List<EmployeeSkill> findBySkillIdInAndYearsOfExperienceGreaterThanEqual(
            @Param("skillIds") List<Long> skillIds,
            @Param("minYearsExperience") Double minYearsExperience);
            
    /**
     * Find employees with a specific skill and level of assessment
     *
     * @param skillId the skill ID
     * @param level the leader assessment level
     * @return list of employee skills matching the criteria
     */
    @Query("SELECT es FROM EmployeeSkill es WHERE es.skill.id = :skillId AND es.leaderAssessmentLevel = :level")
    List<EmployeeSkill> findBySkillIdAndLeaderAssessmentLevel(
            @Param("skillId") Long skillId,
            @Param("level") String level);
            
    /**
     * Find employees with a specific skill and minimum years of experience
     *
     * @param skillId the skill ID
     * @param years the minimum years of experience
     * @return list of employee skills matching the criteria
     */
    @Query("SELECT es FROM EmployeeSkill es WHERE es.skill.id = :skillId AND es.yearsExperience >= :years")
    List<EmployeeSkill> findBySkillIdAndYearsOfExperienceGreaterThanEqual(
            @Param("skillId") Long skillId,
            @Param("years") double years);

    /**
     * Đếm số lượng employee có skillId tương ứng
     * @param skillId id của skill
     * @return số lượng employee
     */
    long countBySkillId(Long skillId);
} 
