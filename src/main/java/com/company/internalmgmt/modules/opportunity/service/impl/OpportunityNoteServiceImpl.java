package com.company.internalmgmt.modules.opportunity.service.impl;

import com.company.internalmgmt.common.dto.PageableInfo;
import com.company.internalmgmt.common.exception.ResourceNotFoundException;
import com.company.internalmgmt.modules.admin.model.User;
import com.company.internalmgmt.modules.opportunity.dto.OpportunityNoteDTO;
import com.company.internalmgmt.modules.opportunity.model.Opportunity;
import com.company.internalmgmt.modules.opportunity.model.OpportunityAttachment;
import com.company.internalmgmt.modules.opportunity.model.OpportunityNote;
import com.company.internalmgmt.modules.opportunity.repository.OpportunityAttachmentRepository;
import com.company.internalmgmt.modules.opportunity.repository.OpportunityNoteRepository;
import com.company.internalmgmt.modules.opportunity.service.OpportunityNoteService;
import com.company.internalmgmt.modules.opportunity.service.OpportunityService;
import com.company.internalmgmt.security.AuthorizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the OpportunityNoteService interface.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OpportunityNoteServiceImpl implements OpportunityNoteService {

    private final OpportunityNoteRepository noteRepository;
    private final OpportunityAttachmentRepository attachmentRepository;
    private final OpportunityService opportunityService;
    private final AuthorizationService authorizationService;
    
    private final String uploadDir = "./uploads/opportunity/notes";

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<OpportunityNoteDTO> getNotesByOpportunity(Long opportunityId, Pageable pageable, PageableInfo pageInfo) {
        // Verify opportunity exists
        Opportunity opportunity = opportunityService.getOpportunityEntityById(opportunityId);
        
        // Get notes with pagination
        Page<OpportunityNote> notesPage = noteRepository.findByOpportunityOrderByCreatedAtDesc(opportunity, pageable);
        
        // Update pagination info
        pageInfo.setPageNumber(notesPage.getNumber() + 1);
        pageInfo.setPageSize(notesPage.getSize());
        pageInfo.setTotalPages(notesPage.getTotalPages());
        pageInfo.setTotalElements(notesPage.getTotalElements());
        
        // Map to DTOs
        return notesPage.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public OpportunityNoteDTO getNoteById(Long noteId) {
        OpportunityNote note = getNoteEntityById(noteId);
        return mapToDTO(note);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public OpportunityNoteDTO createNote(Long opportunityId, OpportunityNoteDTO noteDTO, List<MultipartFile> attachments) {
        // Get opportunity
        Opportunity opportunity = opportunityService.getOpportunityEntityById(opportunityId);
        
        // Get current user
        User currentUser = authorizationService.getCurrentUser();
        
        // Create note entity
        OpportunityNote note = OpportunityNote.builder()
                .opportunity(opportunity)
                .author(currentUser)
                .content(noteDTO.getContent())
                .activityType(noteDTO.getActivityType() != null ? noteDTO.getActivityType() : "note")
                .meetingDate(noteDTO.getMeetingDate())
                .isPrivate(noteDTO.getIsPrivate() != null ? noteDTO.getIsPrivate() : false)
                .build();
        
        // Save note
        OpportunityNote savedNote = noteRepository.save(note);
        
        // Ensure attachments list is initialized to avoid NullPointerException
        if (savedNote.getAttachments() == null) {
            savedNote.setAttachments(new java.util.ArrayList<>());
        }
        
        // Process attachments if any
        if (attachments != null && !attachments.isEmpty()) {
            processAttachments(savedNote, attachments);
        }
        
        // Update opportunity's last interaction date
        opportunity.setLastInteractionDate(LocalDateTime.now());
        opportunityService.getOpportunityEntityById(opportunityId);
        
        // Return DTO
        return mapToDTO(savedNote);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public OpportunityNoteDTO updateNote(Long noteId, OpportunityNoteDTO noteDTO) {
        // Get note entity
        OpportunityNote note = getNoteEntityById(noteId);
        
        // Verify user has permission
        if (!authorizationService.canModifyNote(note)) {
            throw new ResourceNotFoundException("Note not found with id: " + noteId);
        }
        
        // Update fields
        note.setContent(noteDTO.getContent());
        if (noteDTO.getActivityType() != null) {
            note.setActivityType(noteDTO.getActivityType());
        }
        if (noteDTO.getMeetingDate() != null) {
            note.setMeetingDate(noteDTO.getMeetingDate());
        }
        if (noteDTO.getIsPrivate() != null) {
            note.setIsPrivate(noteDTO.getIsPrivate());
        }
        
        // Save changes
        OpportunityNote updatedNote = noteRepository.save(note);
        
        return mapToDTO(updatedNote);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteNote(Long noteId) {
        // Get note entity
        OpportunityNote note = getNoteEntityById(noteId);
        
        // Verify user has permission
        if (!authorizationService.canModifyNote(note)) {
            throw new ResourceNotFoundException("Note not found with id: " + noteId);
        }
        
        // Delete attachments first
        for (OpportunityAttachment attachment : note.getAttachments()) {
            deleteAttachmentFile(attachment.getPath());
        }
        
        // Delete note
        noteRepository.delete(note);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public OpportunityNoteDTO addAttachments(Long noteId, List<MultipartFile> attachments) {
        // Get note entity
        OpportunityNote note = getNoteEntityById(noteId);
        
        // Verify user has permission
        if (!authorizationService.canModifyNote(note)) {
            throw new ResourceNotFoundException("Note not found with id: " + noteId);
        }
        
        // Process attachments
        if (attachments != null && !attachments.isEmpty()) {
            processAttachments(note, attachments);
        }
        
        return mapToDTO(note);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteAttachment(Long attachmentId) {
        // Get attachment
        OpportunityAttachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment not found with id: " + attachmentId));
        
        // Verify user has permission
        if (!authorizationService.canModifyNote(attachment.getNote())) {
            throw new ResourceNotFoundException("Attachment not found with id: " + attachmentId);
        }
        
        // Delete file
        deleteAttachmentFile(attachment.getPath());
        
        // Delete from database
        attachmentRepository.delete(attachment);
    }
    
    /**
     * Get note entity by ID.
     *
     * @param noteId the note ID
     * @return the note entity
     * @throws ResourceNotFoundException if note not found
     */
    private OpportunityNote getNoteEntityById(Long noteId) {
        OpportunityNote note = noteRepository.findById(noteId)
                .orElseThrow(() -> new ResourceNotFoundException("Note not found with id: " + noteId));
        
        // Verify user has permission to view the note
        if (!authorizationService.canAccessNote(note)) {
            throw new ResourceNotFoundException("Note not found with id: " + noteId);
        }
        
        return note;
    }
    
    /**
     * Process attachment files.
     *
     * @param note the note to attach files to
     * @param attachments the files to process
     */
    private void processAttachments(OpportunityNote note, List<MultipartFile> attachments) {
        for (MultipartFile file : attachments) {
            if (file.isEmpty()) {
                continue;
            }
            
            try {
                // Create directories if they don't exist
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                
                // Generate unique filename
                String originalFilename = file.getOriginalFilename();
                String fileExtension = originalFilename != null && originalFilename.contains(".") 
                        ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
                        : "";
                String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
                Path filePath = uploadPath.resolve(uniqueFilename);
                
                // Save file
                Files.copy(file.getInputStream(), filePath);
                
                // Create attachment entity
                OpportunityAttachment attachment = OpportunityAttachment.builder()
                        .note(note)
                        .name(originalFilename)
                        .type(file.getContentType())
                        .size(file.getSize())
                        .path(filePath.toString())
                        .url("/api/v1/opportunity/notes/attachments/" + uniqueFilename)
                        .build();
                
                // Save attachment
                attachmentRepository.save(attachment);
                
            } catch (IOException e) {
                log.error("Failed to save attachment for note {}: {}", note.getId(), e.getMessage());
            }
        }
    }
    
    /**
     * Delete attachment file from disk.
     *
     * @param filePath the path to the file
     */
    private void deleteAttachmentFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("Failed to delete attachment file {}: {}", filePath, e.getMessage());
        }
    }
    
    /**
     * Map note entity to DTO.
     *
     * @param note the note entity
     * @return the note DTO
     */
    private OpportunityNoteDTO mapToDTO(OpportunityNote note) {
        if (note == null) {
            return null;
        }
        
        // Null-safe mapping of attachments
        java.util.List<OpportunityNoteDTO.AttachmentDTO> attachmentDTOs = java.util.Optional.ofNullable(note.getAttachments())
                .orElse(java.util.Collections.emptyList())
                .stream()
                .map(this::mapAttachmentToDTO)
                .collect(Collectors.toList());
        
        return OpportunityNoteDTO.builder()
                .id(note.getId())
                .opportunityId(note.getOpportunity().getId())
                .authorId(note.getAuthor().getId())
                .authorName(note.getAuthor().getUsername())
                .content(note.getContent())
                .activityType(note.getActivityType())
                .meetingDate(note.getMeetingDate())
                .isPrivate(note.getIsPrivate())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .attachments(attachmentDTOs)
                .build();
    }
    
    /**
     * Map attachment entity to DTO.
     *
     * @param attachment the attachment entity
     * @return the attachment DTO
     */
    private OpportunityNoteDTO.AttachmentDTO mapAttachmentToDTO(OpportunityAttachment attachment) {
        return OpportunityNoteDTO.AttachmentDTO.builder()
                .id(attachment.getId())
                .name(attachment.getName())
                .type(attachment.getType())
                .size(attachment.getSize())
                .url(attachment.getUrl())
                .build();
    }
} 