package com.company.internalmgmt.modules.opportunity.model;

import com.company.internalmgmt.common.model.BaseEntity;
import com.company.internalmgmt.modules.admin.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing Hubspot synchronization logs.
 */
@Entity
@Table(name = "opportunity_sync_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OpportunitySyncLog extends BaseEntity {
    
    @Column(name = "sync_id", nullable = false, unique = true, length = 36)
    private String syncId;
    
    @Column(name = "status", nullable = false, length = 50)
    private String status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiated_by_id")
    private User initiatedBy;
    
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "duration")
    private Integer duration;
    
    @Column(name = "sync_mode", length = 20)
    private String syncMode;
    
    @Column(name = "from_date")
    private LocalDateTime fromDate;
    
    @Column(name = "to_date")
    private LocalDateTime toDate;
    
    @Column(name = "deal_stage", length = 50)
    private String dealStage;
    
    @Column(name = "overwrite_existing")
    private Boolean overwriteExisting = false;
    
    @Column(name = "total_opportunities")
    private Integer totalOpportunities;
    
    @Column(name = "new_opportunities")
    private Integer newOpportunities;
    
    @Column(name = "updated_opportunities")
    private Integer updatedOpportunities;
    
    @Column(name = "skipped_opportunities")
    private Integer skippedOpportunities;
    
    @Column(name = "failed_opportunities")
    private Integer failedOpportunities;
    
    @Column(name = "error_code", length = 50)
    private String errorCode;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @OneToMany(mappedBy = "syncLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OpportunitySyncDetail> details = new ArrayList<>();
    
    @OneToMany(mappedBy = "syncLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OpportunitySyncLogEntry> logs = new ArrayList<>();
} 