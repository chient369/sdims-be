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
 * Entity representing notes and interaction logs for opportunities.
 */
@Entity
@Table(name = "opportunity_notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OpportunityNote extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opportunity_id", nullable = false)
    private Opportunity opportunity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "note_content", nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "activity_type", length = 50)
    private String activityType = "note";
    
    @Column(name = "meeting_date")
    private LocalDateTime meetingDate;
    
    @Column(name = "is_private")
    private Boolean isPrivate = false;
    
    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OpportunityAttachment> attachments = new ArrayList<>();
} 