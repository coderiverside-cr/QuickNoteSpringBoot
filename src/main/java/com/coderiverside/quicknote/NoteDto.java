package com.coderiverside.quicknote;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NoteDto(
        Long id,
        
        @NotBlank(message = "Title is required") 
        @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters") 
        String title,
        
        @NotBlank(message = "Content is required") 
        @Size(min = 1, max = 10000, message = "Content must be between 1 and 10000 characters") 
        String content,
        
        @Pattern(regexp = "^(text|checklist|voice)$", message = "Type must be 'text', 'checklist', or 'voice'") 
        String type,
        
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") 
        LocalDateTime creationDate,
        
        boolean isPinned,
        
        boolean isArchived,
        
        @Pattern(regexp = "^(#[0-9A-Fa-f]{6})?$", message = "Color must be a valid hex color code") 
        String color,
        
        String owner) {

    public static NoteDto fromEntity(Note note) {
        return new NoteDto(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getType(),
                note.getCreationDate(),
                note.isPinned(),
                note.isArchived(),
                note.getColor(),
                note.getOwner());
    }

    public Note toEntity(String owner) {
        Note note = new Note();
        note.setTitle(this.title);
        note.setContent(this.content);
        note.setType(this.type);
        note.setCreationDate(this.creationDate);
        note.setPinned(this.isPinned);
        note.setArchived(this.isArchived);
        note.setColor(this.color);
        note.setOwner(owner);
        return note;
    }
}
