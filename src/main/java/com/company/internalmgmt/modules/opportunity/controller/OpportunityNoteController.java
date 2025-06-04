package com.company.internalmgmt.modules.opportunity.controller;

import com.company.internalmgmt.common.dto.ApiResponse;
import com.company.internalmgmt.common.dto.PageableInfo;
import com.company.internalmgmt.modules.opportunity.dto.OpportunityNoteDTO;
import com.company.internalmgmt.modules.opportunity.service.OpportunityNoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * REST controller for managing opportunity notes.
 */
@RestController
@RequestMapping("/api/v1/opportunities")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Opportunity Notes", description = "APIs for managing opportunity notes")
public class OpportunityNoteController {

    private final OpportunityNoteService noteService;

    /**
     * GET /api/v1/opportunities/{opportunityId}/notes : Get all notes for an opportunity.
     *
     * @param opportunityId the ID of the opportunity
     * @param activityType optional filter by activity type
     * @param fromDate optional filter by start date
     * @param toDate optional filter by end date
     * @param createdBy optional filter by creator ID
     * @param includeAttachments whether to include attachment details
     * @param page the page number
     * @param size the page size
     * @param sortBy the field to sort by
     * @param sortDir the sort direction
     * @return the ResponseEntity with status 200 (OK) and the list of notes in the body
     */
    @GetMapping("/{opportunityId}/notes")
    @PreAuthorize("hasAnyAuthority('opportunity-note:read:all', 'opportunity-note:read:assigned')")
    @Operation(summary = "Get all notes for an opportunity", description = "Returns all notes for the specified opportunity with pagination")
    public ResponseEntity<ApiResponse<List<OpportunityNoteDTO>>> getNotesByOpportunity(
            @PathVariable Long opportunityId,
            @RequestParam(required = false) String activityType,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(required = false) Integer createdBy,
            @RequestParam(required = false, defaultValue = "true") Boolean includeAttachments,
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false, defaultValue = "createdAt") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) {

        log.debug("REST request to get notes for opportunity ID: {}", opportunityId);
        
        // Convert page từ 1-based (client) sang 0-based (Spring Data)
        int pageIndex = page > 0 ? page - 1 : 0;
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageIndex, size, Sort.by(direction, sortBy));
        
        PageableInfo pageInfo = new PageableInfo();
        List<OpportunityNoteDTO> notes = noteService.getNotesByOpportunity(opportunityId, pageable, pageInfo);
        
        ApiResponse<List<OpportunityNoteDTO>> response = ApiResponse.success(notes, pageInfo);
        
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/opportunities/notes/{id} : Get a specific note by ID.
     *
     * @param id the ID of the note to retrieve
     * @return the ResponseEntity with status 200 (OK) and the note in the body
     */
    @GetMapping("/notes/{id}")
    @PreAuthorize("hasAnyAuthority('opportunity-note:read:all', 'opportunity-note:read:assigned')")
    @Operation(summary = "Get a note by ID", description = "Returns a specific note by its ID")
    public ResponseEntity<ApiResponse<OpportunityNoteDTO>> getNoteById(@PathVariable Long id) {
        log.debug("REST request to get note ID: {}", id);
        OpportunityNoteDTO note = noteService.getNoteById(id);
        ApiResponse<OpportunityNoteDTO> response = ApiResponse.success(note);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/opportunities/{opportunityId}/notes : Create a new note for an opportunity.
     *
     * @param opportunityId the ID of the opportunity
     * @param noteDTO the note to create
     * @param attachments optional file attachments
     * @return the ResponseEntity with status 201 (Created) and the new note in the body
     */
    @PostMapping(value = "/{opportunityId}/notes", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('opportunity-note:create:all', 'opportunity-note:create:assigned')")
    @Operation(summary = "Create a new note with attachments", description = "Creates a new note with file attachments for the specified opportunity")
    public ResponseEntity<ApiResponse<OpportunityNoteDTO>> createNoteWithAttachments(
            @PathVariable Long opportunityId,
            @Valid @RequestPart("note") OpportunityNoteDTO noteDTO,
            @RequestPart(value = "attachments", required = false) List<MultipartFile> attachments) {
        
        log.debug("REST request to create note for opportunity ID: {} with attachments", opportunityId);
        OpportunityNoteDTO createdNote = noteService.createNote(opportunityId, noteDTO, attachments);
        ApiResponse<OpportunityNoteDTO> response = ApiResponse.success(createdNote, 201);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PUT /api/v1/opportunities/notes/{id} : Update an existing note.
     *
     * @param id the ID of the note to update
     * @param noteDTO the updated note data
     * @return the ResponseEntity with status 200 (OK) and the updated note in the body
     */
    @PutMapping("/notes/{id}")
    @PreAuthorize("hasAnyAuthority('opportunity-note:create:all', 'opportunity-note:create:assigned')")
    @Operation(summary = "Update a note", description = "Updates an existing note")
    public ResponseEntity<ApiResponse<OpportunityNoteDTO>> updateNote(
            @PathVariable Long id,
            @Valid @RequestBody OpportunityNoteDTO noteDTO) {
        
        log.debug("REST request to update note ID: {}", id);
        OpportunityNoteDTO updatedNote = noteService.updateNote(id, noteDTO);
        ApiResponse<OpportunityNoteDTO> response = ApiResponse.success(updatedNote);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/v1/opportunities/notes/{id} : Delete a note.
     *
     * @param id the ID of the note to delete
     * @return the ResponseEntity with status 204 (No Content)
     */
    @DeleteMapping("/notes/{id}")
    @PreAuthorize("hasAnyAuthority('opportunity-note:create:all', 'opportunity-note:create:assigned')")
    @Operation(summary = "Delete a note", description = "Deletes an existing note")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id) {
        log.debug("REST request to delete note ID: {}", id);
        noteService.deleteNote(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/v1/opportunities/{opportunityId}/notes : Create a new note for an opportunity (JSON only).
     *
     * @param opportunityId the ID of the opportunity
     * @param noteDTO the note to create
     * @return the ResponseEntity with status 201 (Created) and the new note in the body
     */
    @PostMapping(value = "/{opportunityId}/notes", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('opportunity-note:create:all', 'opportunity-note:create:assigned')")
    @Operation(summary = "Create a new note (JSON only)", description = "Creates a new note without attachments using JSON body")
    public ResponseEntity<ApiResponse<OpportunityNoteDTO>> createNoteJson(
            @PathVariable Long opportunityId,
            @Valid @RequestBody OpportunityNoteDTO noteDTO) {
        log.debug("REST request to create note for opportunity ID: {} (JSON only)", opportunityId);
        OpportunityNoteDTO createdNote = noteService.createNote(opportunityId, noteDTO, null);
        ApiResponse<OpportunityNoteDTO> response = ApiResponse.success(createdNote, 201);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
} 