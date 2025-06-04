package com.company.internalmgmt.modules.opportunity.repository;

import com.company.internalmgmt.modules.opportunity.model.OpportunitySyncLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository for OpportunitySyncLog entity.
 */
@Repository
public interface OpportunitySyncLogRepository extends JpaRepository<OpportunitySyncLog, Long> {
    
    /**
     * Find sync log by sync ID.
     *
     * @param syncId the sync ID
     * @return optional sync log
     */
    Optional<OpportunitySyncLog> findBySyncId(String syncId);
    
    /**
     * Find sync logs with filters.
     *
     * @param status the status filter
     * @param fromDate the from date filter
     * @param toDate the to date filter
     * @param pageable the pagination information
     * @return page of sync logs
     */
    @Query("SELECT s FROM OpportunitySyncLog s WHERE " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:fromDate IS NULL OR s.startedAt >= :fromDate) AND " +
           "(:toDate IS NULL OR s.startedAt <= :toDate) " +
           "ORDER BY s.startedAt DESC")
    Page<OpportunitySyncLog> findSyncLogs(
            @Param("status") String status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);
} 