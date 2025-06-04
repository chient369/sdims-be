package com.company.internalmgmt.modules.opportunity.repository;

import com.company.internalmgmt.modules.opportunity.model.OpportunityAttachment;
import com.company.internalmgmt.modules.opportunity.model.OpportunityNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for OpportunityAttachment entities.
 */
@Repository
public interface OpportunityAttachmentRepository extends JpaRepository<OpportunityAttachment, Long> {
    
    /**
     * Find all attachments for a specific note.
     *
     * @param note the note
     * @return list of attachments
     */
    List<OpportunityAttachment> findByNote(OpportunityNote note);
    
    /**
     * Find attachment by filename.
     *
     * @param name the filename
     * @return the attachment if found
     */
    OpportunityAttachment findByName(String name);
    
    /**
     * Delete all attachments for a specific note.
     *
     * @param note the note
     */
    void deleteByNote(OpportunityNote note);
} 