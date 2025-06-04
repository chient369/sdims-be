package com.company.internalmgmt.modules.opportunity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO for OpportunityNote data.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpportunityNoteDTO {
    
    /**
     * The note ID.
     */
    private Long id;
    
    /**
     * The ID of the opportunity this note belongs to.
     */
    private Long opportunityId;
    
    /**
     * The ID of the author (user) who created the note.
     */
    private Long authorId;
    
    /**
     * The name of the author.
     */
    private String authorName;
    
    /**
     * The content of the note.
     */
    private String content;
    
    /**
     * The type of activity this note represents.
     * Examples: "note", "meeting", "call", "email", etc.
     */
    private String activityType;
    
    /**
     * Date of the meeting, if this note represents a meeting.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime meetingDate;
    
    /**
     * Whether this note is private.
     */
    private Boolean isPrivate;
    
    /**
     * Creation timestamp.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    /**
     * Last update timestamp.
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    /**
     * List of attachments.
     */
    private List<AttachmentDTO> attachments = new ArrayList<>();
    
    /**
     * DTO for attachment data.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttachmentDTO {
        private Long id;
        private String name;
        private String type;
        private Long size;
        private String url;
    }
} 