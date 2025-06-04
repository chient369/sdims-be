package com.company.internalmgmt.modules.opportunity.model;

import com.company.internalmgmt.common.model.BaseEntity;
import com.company.internalmgmt.modules.admin.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing business opportunities, potentially synced from Hubspot.
 */
@Entity
@Table(name = "opportunities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Where(clause = "deleted_at IS NULL")
public class Opportunity extends BaseEntity {

    @Column(name = "code", unique = true, length = 50)
    private String code;
    
    @Column(name = "hubspot_id", unique = true)
    private String hubspotId;

    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "client_name", length = 255)
    private String clientName;
    
    @Column(name = "client_contact", length = 255)
    private String clientContact;
    
    @Column(name = "client_email", length = 255)
    private String clientEmail;
    
    @Column(name = "client_phone", length = 50)
    private String clientPhone;
    
    @Column(name = "client_address", length = 500)
    private String clientAddress;
    
    @Column(name = "client_website", length = 255)
    private String clientWebsite;
    
    @Column(name = "client_industry", length = 100)
    private String clientIndustry;

    @Column(name = "estimated_value", precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "currency", length = 3)
    private String currency;

    @Column(name = "deal_stage", length = 100)
    private String status;
    
    @Column(name = "deal_size", length = 20)
    private String dealSize;

    @Column(name = "source", length = 100)
    private String source = "HUBSPOT";
    
    @Column(name = "external_id", length = 100)
    private String externalId;
    
    @Column(name = "closing_date")
    private LocalDateTime closingDate;
    
    @Column(name = "closing_probability")
    private Integer closingProbability;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_sales_id")
    private User assignedTo;

    @Column(name = "last_interaction_date")
    private LocalDateTime lastInteractionDate;

    @Column(name = "onsite_priority")
    private Boolean priority = false;

    @Column(name = "follow_up_status", length = 50)
    private String followUpStatus;
    
    @Column(name = "hubspot_created_at")
    private LocalDateTime hubspotCreatedAt;

    @Column(name = "hubspot_last_updated_at")
    private LocalDateTime hubspotLastUpdatedAt;

    @Column(name = "sync_status", length = 50)
    private String syncStatus;

    @Column(name = "last_sync_at")
    private LocalDateTime lastSyncAt;
    
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    
    @ElementCollection
    @CollectionTable(name = "opportunity_tags", joinColumns = @JoinColumn(name = "opportunity_id"))
    @Column(name = "tag", length = 50)
    private List<String> tags = new ArrayList<>();

    @OneToMany(mappedBy = "opportunity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OpportunityAssignment> assignments = new ArrayList<>();

    @OneToMany(mappedBy = "opportunity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OpportunityNote> notes = new ArrayList<>();
    
    @OneToMany(mappedBy = "opportunity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OpportunityRequirement> requirements = new ArrayList<>();
} 