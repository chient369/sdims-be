package com.company.internalmgmt.modules.hrm.repository.specification;

import com.company.internalmgmt.modules.hrm.model.Employee;
import com.company.internalmgmt.modules.hrm.model.EmployeeSkill;
import com.company.internalmgmt.modules.hrm.model.Skill;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Subquery;

import java.util.ArrayList;
import java.util.List;

/**
 * Specification class for advanced Employee searching
 */
public class EmployeeSpecification {

    /**
     * Create a specification to search employees by name (first name or last name)
     * @param name the name to search for
     * @return specification for searching by name
     */
    public static Specification<Employee> nameLike(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isEmpty()) {
                return cb.conjunction();
            }
            String nameLower = "%" + name.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("firstName")), nameLower),
                cb.like(cb.lower(root.get("lastName")), nameLower)
            );
        };
    }

    /**
     * Create a specification to filter employees by status
     * @param status the status to filter by
     * @return specification for filtering by status
     */
    public static Specification<Employee> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("currentStatus"), status);
        };
    }

    /**
     * Create a specification to filter employees by team ID
     * @param teamId the team ID to filter by
     * @return specification for filtering by team
     */
    public static Specification<Employee> inTeam(Long teamId) {
        return (root, query, cb) -> {
            if (teamId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("team").get("id"), teamId);
        };
    }

    /**
     * Create a specification to filter employees by position
     * @param position the position to filter by
     * @return specification for filtering by position
     */
    public static Specification<Employee> hasPosition(String position) {
        return (root, query, cb) -> {
            if (position == null || position.isEmpty()) {
                return cb.conjunction();
            }
            return cb.equal(root.get("position"), position);
        };
    }

    /**
     * Create a specification to filter employees by reporting leader
     * @param leaderId the leader ID to filter by
     * @return specification for filtering by reporting leader
     */
    public static Specification<Employee> hasReportingLeader(Long leaderId) {
        return (root, query, cb) -> {
            if (leaderId == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("reportingLeader").get("id"), leaderId);
        };
    }

    /**
     * Create a specification to filter employees by skills
     * @param skillIds the list of skill IDs to filter by
     * @return specification for filtering by skills (employee must have ALL skills)
     */
    public static Specification<Employee> hasAllSkills(List<Long> skillIds) {
        return (root, query, cb) -> {
            if (skillIds == null || skillIds.isEmpty()) {
                return cb.conjunction();
            }
            
            // Use distinct to avoid duplicate employees
            query.distinct(true);
            
            // For each skill, we need to verify the employee has it
            List<Predicate> skillPredicates = new ArrayList<>();
            
            for (Long skillId : skillIds) {
                // Create a subquery for each skill
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<EmployeeSkill> esRoot = subquery.from(EmployeeSkill.class);
                
                // The subquery selects employee IDs that have this skill
                subquery.select(esRoot.get("employee").get("id"))
                        .where(
                            cb.and(
                                cb.equal(esRoot.get("employee").get("id"), root.get("id")),
                                cb.equal(esRoot.get("skill").get("id"), skillId)
                            )
                        );
                
                // Add a predicate that checks the subquery exists
                skillPredicates.add(cb.exists(subquery));
            }
            
            // Combine all skill predicates with AND (employee must have ALL skills)
            return cb.and(skillPredicates.toArray(new Predicate[0]));
        };
    }
    
    /**
     * Create a specification to filter employees by skill level (self-assessment or leader-assessment)
     * @param skillId the skill ID
     * @param level the skill level (Basic, Intermediate, Advanced)
     * @return specification for filtering by skill level
     */
    public static Specification<Employee> hasSkillLevel(Long skillId, String level) {
        return (root, query, cb) -> {
            if (skillId == null || level == null || level.isEmpty()) {
                return cb.conjunction();
            }
            
            query.distinct(true);
            
            // Join with EmployeeSkill
            Join<Employee, EmployeeSkill> skillJoin = root.join("skills", JoinType.INNER);
            
            // Filter by skill ID and level
            return cb.and(
                cb.equal(skillJoin.get("skill").get("id"), skillId),
                cb.or(
                    cb.equal(skillJoin.get("selfAssessmentLevel"), level),
                    cb.equal(skillJoin.get("leaderAssessmentLevel"), level)
                )
            );
        };
    }
    
    /**
     * Create a specification to filter employees by minimum years of experience in a skill
     * @param skillId the skill ID
     * @param years the minimum years of experience
     * @return specification for filtering by years of experience
     */
    public static Specification<Employee> hasMinYearsExperience(Long skillId, Double years) {
        return (root, query, cb) -> {
            if (skillId == null || years == null) {
                return cb.conjunction();
            }
            
            query.distinct(true);
            
            // Join with EmployeeSkill
            Join<Employee, EmployeeSkill> skillJoin = root.join("skills", JoinType.INNER);
            
            // Filter by skill ID and minimum years
            return cb.and(
                cb.equal(skillJoin.get("skill").get("id"), skillId),
                cb.greaterThanOrEqualTo(skillJoin.get("yearsExperience"), years)
            );
        };
    }
} 
