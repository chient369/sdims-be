package com.company.internalmgmt.modules.opportunity.repository.specification;

import com.company.internalmgmt.modules.admin.model.User;
import com.company.internalmgmt.modules.hrm.model.Employee;
import com.company.internalmgmt.modules.opportunity.model.Opportunity;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Specification class for filtering Opportunity entities.
 */
public class OpportunitySpecification {

    /**
     * Filter opportunities by keyword in name, code, client name, or description.
     *
     * @param keyword the search keyword
     * @return the specification
     */
    public static Specification<Opportunity> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("name")), likePattern),
                cb.like(cb.lower(root.get("code")), likePattern),
                cb.like(cb.lower(root.get("clientName")), likePattern),
                cb.like(cb.lower(root.get("description")), likePattern)
            );
        };
    }

    /**
     * Filter opportunities by status.
     *
     * @param statuses list of statuses
     * @return the specification
     */
    public static Specification<Opportunity> hasStatus(List<String> statuses) {
        return (root, query, cb) -> root.get("status").in(statuses);
    }

    /**
     * Filter opportunities by value range.
     *
     * @param minValue minimum value
     * @param maxValue maximum value
     * @return the specification
     */
    public static Specification<Opportunity> hasValueBetween(BigDecimal minValue, BigDecimal maxValue) {
        return (root, query, cb) -> {
            if (minValue != null && maxValue != null) {
                return cb.between(root.get("amount"), minValue, maxValue);
            } else if (minValue != null) {
                return cb.greaterThanOrEqualTo(root.get("amount"), minValue);
            } else if (maxValue != null) {
                return cb.lessThanOrEqualTo(root.get("amount"), maxValue);
            }
            return null;
        };
    }

    /**
     * Filter opportunities by priority.
     *
     * @param priority the priority
     * @return the specification
     */
    public static Specification<Opportunity> hasPriority(Boolean priority) {
        return (root, query, cb) -> cb.equal(root.get("priority"), priority);
    }

    /**
     * Filter opportunities by assigned user.
     *
     * @param userId the user ID
     * @return the specification
     */
    public static Specification<Opportunity> hasAssignedTo(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("assignedTo").get("id"), userId);
    }

    /**
     * Filter opportunities by creator.
     *
     * @param userId the user ID
     * @return the specification
     */
    public static Specification<Opportunity> hasCreatedBy(Long userId) {
        return (root, query, cb) -> cb.equal(root.get("createdBy").get("id"), userId);
    }

    /**
     * Filter opportunities by created date range.
     *
     * @param fromDate the start date
     * @param toDate the end date
     * @return the specification
     */
    public static Specification<Opportunity> hasCreatedDateBetween(LocalDateTime fromDate, LocalDateTime toDate) {
        return (root, query, cb) -> {
            if (fromDate != null && toDate != null) {
                return cb.between(root.get("createdAt"), fromDate, toDate);
            } else if (fromDate != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate);
            } else if (toDate != null) {
                return cb.lessThanOrEqualTo(root.get("createdAt"), toDate);
            }
            return null;
        };
    }

    /**
     * Filter opportunities by last interaction date range.
     *
     * @param fromDate the start date
     * @param toDate the end date
     * @return the specification
     */
    public static Specification<Opportunity> hasLastInteractionDateBetween(LocalDateTime fromDate, LocalDateTime toDate) {
        return (root, query, cb) -> {
            if (fromDate != null && toDate != null) {
                return cb.between(root.get("lastInteractionDate"), fromDate, toDate);
            } else if (fromDate != null) {
                return cb.greaterThanOrEqualTo(root.get("lastInteractionDate"), fromDate);
            } else if (toDate != null) {
                return cb.lessThanOrEqualTo(root.get("lastInteractionDate"), toDate);
            }
            return null;
        };
    }

    /**
     * Filter opportunities by deal size.
     *
     * @param dealSizes list of deal sizes
     * @return the specification
     */
    public static Specification<Opportunity> hasDealSize(List<String> dealSizes) {
        return (root, query, cb) -> {
            if (dealSizes == null || dealSizes.isEmpty()) {
                return null;
            }
            return root.get("dealSize").in(dealSizes);
        };
    }

    /**
     * Filter opportunities by employee assignment.
     *
     * @param employeeId the employee ID
     * @return the specification
     */
    public static Specification<Opportunity> hasEmployee(Long employeeId) {
        return (root, query, cb) -> {
            Join<Object, Object> assignments = root.join("assignments", JoinType.INNER);
            return cb.equal(assignments.get("employee").get("id"), employeeId);
        };
    }
} 