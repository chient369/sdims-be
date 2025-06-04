package com.company.internalmgmt.modules.opportunity.repository.specification;

import com.company.internalmgmt.modules.opportunity.model.OpportunityNote;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Join;
import java.time.LocalDateTime;

/**
 * Specification class for filtering OpportunityNote entities.
 */
public class OpportunityNoteSpecification {

    /**
     * Filter notes by content keyword.
     *
     * @param keyword the search keyword
     * @return the specification
     */
    public static Specification<OpportunityNote> hasContentKeyword(String keyword) {
        return (root, query, cb) -> {
            String likePattern = "%" + keyword.toLowerCase() + "%";
            return cb.like(cb.lower(root.get("content")), likePattern);
        };
    }

    /**
     * Filter notes by opportunity ID.
     *
     * @param opportunityId the opportunity ID
     * @return the specification
     */
    public static Specification<OpportunityNote> hasOpportunityId(Long opportunityId) {
        return (root, query, cb) -> 
            cb.equal(root.get("opportunity").get("id"), opportunityId);
    }

    /**
     * Filter notes by author ID.
     *
     * @param authorId the author ID
     * @return the specification
     */
    public static Specification<OpportunityNote> hasAuthorId(Long authorId) {
        return (root, query, cb) -> 
            cb.equal(root.get("author").get("id"), authorId);
    }

    /**
     * Filter notes by activity type.
     *
     * @param activityType the activity type
     * @return the specification
     */
    public static Specification<OpportunityNote> hasActivityType(String activityType) {
        return (root, query, cb) -> 
            cb.equal(root.get("activityType"), activityType);
    }

    /**
     * Filter notes by privacy.
     *
     * @param isPrivate whether the note is private
     * @return the specification
     */
    public static Specification<OpportunityNote> isPrivate(Boolean isPrivate) {
        return (root, query, cb) -> 
            cb.equal(root.get("isPrivate"), isPrivate);
    }

    /**
     * Filter notes by created date range.
     *
     * @param fromDate the start date
     * @param toDate the end date
     * @return the specification
     */
    public static Specification<OpportunityNote> hasCreatedDateBetween(LocalDateTime fromDate, LocalDateTime toDate) {
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
     * Filter notes by meeting date range.
     *
     * @param fromDate the start date
     * @param toDate the end date
     * @return the specification
     */
    public static Specification<OpportunityNote> hasMeetingDateBetween(LocalDateTime fromDate, LocalDateTime toDate) {
        return (root, query, cb) -> {
            if (fromDate != null && toDate != null) {
                return cb.between(root.get("meetingDate"), fromDate, toDate);
            } else if (fromDate != null) {
                return cb.greaterThanOrEqualTo(root.get("meetingDate"), fromDate);
            } else if (toDate != null) {
                return cb.lessThanOrEqualTo(root.get("meetingDate"), toDate);
            }
            return null;
        };
    }
} 