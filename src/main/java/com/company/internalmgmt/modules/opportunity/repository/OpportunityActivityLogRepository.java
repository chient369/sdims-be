package com.company.internalmgmt.modules.opportunity.repository;

import com.company.internalmgmt.modules.opportunity.model.Opportunity;
import com.company.internalmgmt.modules.opportunity.model.OpportunityActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for OpportunityActivityLog entities
 */
@Repository
public interface OpportunityActivityLogRepository extends JpaRepository<OpportunityActivityLog, Long> {

    /**
     * Find activity logs for a specific opportunity
     * 
     * @param opportunity the opportunity
     * @param pageable pagination information
     * @return page of activity logs
     */
    Page<OpportunityActivityLog> findByOpportunityOrderByActivityTimestampDesc(Opportunity opportunity, Pageable pageable);

    /**
     * Find activity logs for a specific opportunity
     * 
     * @param opportunity the opportunity
     * @return list of activity logs
     */
    List<OpportunityActivityLog> findByOpportunityOrderByActivityTimestampDesc(Opportunity opportunity);

    /**
     * Find activity logs by activity type
     * 
     * @param activityType the activity type
     * @return list of activity logs
     */
    List<OpportunityActivityLog> findByActivityType(String activityType);

    /**
     * Find activity logs within a date range
     * 
     * @param startDate the start date
     * @param endDate the end date
     * @return list of activity logs
     */
    @Query("SELECT al FROM OpportunityActivityLog al WHERE al.activityTimestamp BETWEEN :startDate AND :endDate ORDER BY al.activityTimestamp DESC")
    List<OpportunityActivityLog> findByActivityTimestampBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
} 