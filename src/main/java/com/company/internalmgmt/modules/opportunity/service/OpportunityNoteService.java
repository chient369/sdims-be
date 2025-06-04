package com.company.internalmgmt.modules.opportunity.service;

import com.company.internalmgmt.common.dto.PageableInfo;
import com.company.internalmgmt.modules.opportunity.dto.OpportunityNoteDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service interface for managing opportunity notes.
 */
public interface OpportunityNoteService {

    /**
     * Get notes for an opportunity with pagination.
     *
     * @param opportunityId the opportunity ID
     * @param pageable pagination information
     * @return page of notes
     */
    List<OpportunityNoteDTO> getNotesByOpportunity(Long opportunityId, Pageable pageable, PageableInfo pageInfo);

    /**
     * Get a note by ID.
     *
     * @param noteId the note ID
     * @return the note DTO
     */
    OpportunityNoteDTO getNoteById(Long noteId);

    /**
     * Create a new note for an opportunity.
     *
     * @param opportunityId the opportunity ID
     * @param noteDTO the note data
     * @param attachments optional attachments
     * @return the created note DTO
     */
    OpportunityNoteDTO createNote(Long opportunityId, OpportunityNoteDTO noteDTO, List<MultipartFile> attachments);

    /**
     * Update an existing note.
     *
     * @param noteId the note ID
     * @param noteDTO the updated note data
     * @return the updated note DTO
     */
    OpportunityNoteDTO updateNote(Long noteId, OpportunityNoteDTO noteDTO);

    /**
     * Delete a note.
     *
     * @param noteId the note ID
     */
    void deleteNote(Long noteId);

    /**
     * Add attachments to a note.
     *
     * @param noteId the note ID
     * @param attachments the files to attach
     * @return the updated note DTO
     */
    OpportunityNoteDTO addAttachments(Long noteId, List<MultipartFile> attachments);

    /**
     * Delete an attachment.
     *
     * @param attachmentId the attachment ID
     */
    void deleteAttachment(Long attachmentId);
} 