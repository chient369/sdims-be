package com.company.internalmgmt.modules.opportunity.model;

import com.company.internalmgmt.common.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

/**
 * Entity representing file attachments for opportunity notes.
 */
@Entity
@Table(name = "opportunity_attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class OpportunityAttachment extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_id", nullable = false)
    private OpportunityNote note;
    
    @Column(name = "file_name", nullable = false, length = 255)
    private String name;
    
    @Column(name = "file_type", length = 100)
    private String type;
    
    @Column(name = "file_size")
    private Long size;
    
    @Column(name = "file_path", nullable = false, length = 500)
    private String path;
    
    @Column(name = "public_url", length = 500)
    private String url;
} 