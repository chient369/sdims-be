package com.company.internalmgmt.modules.opportunity.model;

import com.company.internalmgmt.common.model.BaseEntity;
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
 * Entity representing details of opportunity synchronization.
 */
@Entity
@Table(name = "opportunity_sync_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OpportunitySyncDetail extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sync_log_id", nullable = false)
    private OpportunitySyncLog syncLog;
    
    @Column(name = "hubspot_deal_id", nullable = false, length = 100)
    private String hubspotDealId;
    
    @Column(name = "action", nullable = false, length = 50)
    private String action;
    
    @Column(name = "status", nullable = false, length = 50)
    private String status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id")
    private Opportunity opportunity;
    
    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;
    
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
    
    @OneToMany(mappedBy = "syncDetail", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OpportunityFieldChange> changes = new ArrayList<>();
} 