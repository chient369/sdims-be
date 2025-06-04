package com.company.internalmgmt.modules.hrm.repository.specification;

import com.company.internalmgmt.modules.hrm.model.Employee;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Specifications for Employee entity queries
 */
public class EmployeeSpecifications {

    private EmployeeSpecifications() {
        // Utility class
    }

    /**
     * Create specification for filtering employees by various criteria
     *
     * @param employeeCode the employee code
     * @param name the employee name (first or last)
     * @param email the employee email
     * @param position the employee position
     * @param team the employee team
     * @param reportingLeaderId the reporting leader ID
     * @param status the employee status
     * @param hireDateFrom the hire date from
     * @param hireDateTo the hire date to
     * @return the specification
     */
    public static Specification<Employee> filterBy(
            String employeeCode,
            String name,
            String email,
            String position,
            String team,
            Long reportingLeaderId,
            String status,
            LocalDate hireDateFrom,
            LocalDate hireDateTo) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by employee code
            if (StringUtils.hasText(employeeCode)) {
                predicates.add(cb.like(cb.lower(root.get("employeeCode")), "%" + employeeCode.toLowerCase() + "%"));
            }

            // Filter by name (first or last)
            if (StringUtils.hasText(name)) {
                String nameLike = "%" + name.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("firstName")), nameLike),
                        cb.like(cb.lower(root.get("lastName")), nameLike)
                ));
            }

            // Filter by email
            if (StringUtils.hasText(email)) {
                predicates.add(cb.like(cb.lower(root.get("companyEmail")), "%" + email.toLowerCase() + "%"));
            }

            // Filter by position
            if (StringUtils.hasText(position)) {
                predicates.add(cb.like(cb.lower(root.get("position")), "%" + position.toLowerCase() + "%"));
            }

            // Filter by team
            if (StringUtils.hasText(team)) {
                predicates.add(cb.like(cb.lower(root.get("team")), "%" + team.toLowerCase() + "%"));
            }

            // Filter by reporting leader ID
            if (reportingLeaderId != null) {
                predicates.add(cb.equal(root.get("reportingLeaderId"), reportingLeaderId));
            }

            // Filter by status
            if (StringUtils.hasText(status)) {
                predicates.add(cb.equal(root.get("currentStatus"), status));
            }

            // Filter by hire date range
            if (hireDateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("hireDate"), hireDateFrom));
            }
            if (hireDateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("hireDate"), hireDateTo));
            }

            // Only include non-deleted employees
            predicates.add(cb.isNull(root.get("deletedAt")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Create specification for searching employees by keyword
     *
     * @param keyword the search keyword
     * @return the specification
     */
    public static Specification<Employee> search(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        String searchTerm = "%" + keyword.toLowerCase() + "%";

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.like(cb.lower(root.get("employeeCode")), searchTerm));
            predicates.add(cb.like(cb.lower(root.get("firstName")), searchTerm));
            predicates.add(cb.like(cb.lower(root.get("lastName")), searchTerm));
            predicates.add(cb.like(cb.lower(root.get("companyEmail")), searchTerm));
            predicates.add(cb.like(cb.lower(root.get("position")), searchTerm));
            predicates.add(cb.like(cb.lower(root.get("team")), searchTerm));

            // Only include non-deleted employees
            Predicate searchPredicates = cb.or(predicates.toArray(new Predicate[0]));
            Predicate notDeleted = cb.isNull(root.get("deletedAt"));

            return cb.and(searchPredicates, notDeleted);
        };
    }
    
    /**
     * Create specification for finding employees by name
     *
     * @param name the name to search for
     * @return the specification
     */
    public static Specification<Employee> nameContains(String name) {
        if (!StringUtils.hasText(name)) {
            return null;
        }
        
        String nameLike = "%" + name.toLowerCase() + "%";
        
        return (root, query, cb) -> {
            Predicate firstNamePredicate = cb.like(cb.lower(root.get("firstName")), nameLike);
            Predicate lastNamePredicate = cb.like(cb.lower(root.get("lastName")), nameLike);
            Predicate employeeCodePredicate = cb.like(cb.lower(root.get("employeeCode")), nameLike);
            
            return cb.or(firstNamePredicate, lastNamePredicate, employeeCodePredicate);
        };
    }
    
    /**
     * Create specification for finding employees by team
     *
     * @param team the team to search for
     * @return the specification
     */
    public static Specification<Employee> teamEquals(String team) {
        if (!StringUtils.hasText(team)) {
            return null;
        }
        
        return (root, query, cb) -> cb.equal(root.get("team"), team);
    }
    
    /**
     * Create specification for finding employees by team ID
     *
     * @param teamId the team ID to search for
     * @return the specification
     */
    public static Specification<Employee> inTeam(Long teamId) {
        if (teamId == null) {
            return null;
        }
        
        return (root, query, cb) -> cb.equal(root.get("team").get("id"), teamId);
    }
    
    /**
     * Create specification for finding employees by status
     *
     * @param status the status to search for
     * @return the specification
     */
    public static Specification<Employee> statusEquals(String status) {
        if (!StringUtils.hasText(status)) {
            return null;
        }
        
        return (root, query, cb) -> cb.equal(root.get("currentStatus"), status);
    }
} 