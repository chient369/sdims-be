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

/**
 * Entity representing activity logs for opportunities
 */
@Entity
@Table(name = "opportunity_activity_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OpportunityActivityLog extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id", nullable = false)
    private Opportunity opportunity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "activity_type", nullable = false, length = 50)
    private String activityType;

    @Column(name = "activity_description", nullable = false)
    private String activityDescription;

    @Column(name = "old_value")
    private String oldValue;

    @Column(name = "new_value")
    private String newValue;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "activity_timestamp", nullable = false)
    private LocalDateTime activityTimestamp;

    @PrePersist
    protected void onCreate() {
        if (activityTimestamp == null) {
            activityTimestamp = LocalDateTime.now();
        }
    }
} 