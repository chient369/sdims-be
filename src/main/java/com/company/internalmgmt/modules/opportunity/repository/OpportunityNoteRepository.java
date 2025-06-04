package com.company.internalmgmt.modules.opportunity.repository;

import com.company.internalmgmt.modules.admin.model.User;
import com.company.internalmgmt.modules.opportunity.model.Opportunity;
import com.company.internalmgmt.modules.opportunity.model.OpportunityNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for OpportunityNote entities.
 */
@Repository
public interface OpportunityNoteRepository extends JpaRepository<OpportunityNote, Long>, JpaSpecificationExecutor<OpportunityNote> {

    /**
     * Find all notes for a specific opportunity with pagination.
     * 
     * @param opportunity the opportunity
     * @param pageable pagination information
     * @return page of notes
     */
    Page<OpportunityNote> findByOpportunity(Opportunity opportunity, Pageable pageable);
    
    /**
     * Find all notes for a specific opportunity created by a specific author.
     * 
     * @param opportunity the opportunity
     * @param author the author
     * @return list of notes
     */
    List<OpportunityNote> findByOpportunityAndAuthor(Opportunity opportunity, User author);
    
    /**
     * Find all notes for a specific opportunity within a date range.
     * 
     * @param opportunity the opportunity
     * @param startDate the start date
     * @param endDate the end date
     * @return list of notes
     */
    List<OpportunityNote> findByOpportunityAndCreatedAtBetween(
        Opportunity opportunity, LocalDateTime startDate, LocalDateTime endDate);
        
    /**
     * Find all notes for a specific opportunity by activity type.
     * 
     * @param opportunity the opportunity
     * @param activityType the activity type
     * @return list of notes
     */
    List<OpportunityNote> findByOpportunityAndActivityType(Opportunity opportunity, String activityType);

    /**
     * Find notes by opportunity
     * 
     * @param opportunity the opportunity
     * @param pageable the pageable
     * @return the page of notes
     */
    Page<OpportunityNote> findByOpportunityOrderByCreatedAtDesc(Opportunity opportunity, Pageable pageable);
    
    /**
     * Find notes by author
     * 
     * @param author the author
     * @param pageable the pageable
     * @return the page of notes
     */
    Page<OpportunityNote> findByAuthorOrderByCreatedAtDesc(User author, Pageable pageable);
    
    /**
     * Find notes by opportunity and author
     * 
     * @param opportunity the opportunity
     * @param author the author
     * @param pageable the pageable
     * @return the page of notes
     */
    Page<OpportunityNote> findByOpportunityAndAuthorOrderByCreatedAtDesc(Opportunity opportunity, User author, Pageable pageable);
    
    /**
     * Find notes with meeting date in a range
     * 
     * @param start the start date
     * @param end the end date
     * @param pageable the pageable
     * @return the page of notes
     */
    Page<OpportunityNote> findByMeetingDateBetweenOrderByMeetingDateDesc(LocalDateTime start, LocalDateTime end, Pageable pageable);
} 