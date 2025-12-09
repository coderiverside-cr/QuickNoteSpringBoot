package com.coderiverside.quicknote;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.Valid;

/**
 * DTO que representa una nota completa con todas sus relaciones.
 * Se usa tanto para entrada (creación) como para salida (lectura de
 * relaciones).
 */
public record NoteExtendedDto(
        Long id,
        @NotBlank(message = "Title is required") @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters") String title,
        @NotBlank(message = "Content is required") @Size(min = 1, max = 10000, message = "Content must be between 1 and 10000 characters") String content,
        @Pattern(regexp = "^(text|checklist|voice)$", message = "Type must be 'text', 'checklist', or 'voice'") String type,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") LocalDateTime creationDate,
        boolean isPinned,
        boolean isArchived,
        @Pattern(regexp = "^(#[0-9A-Fa-f]{6})?$", message = "Color must be a valid hex color code") String color,
        String owner,
        @Valid List<ReminderDto> reminders,
        @Valid NoteSettingsDto settings,
        List<String> labels) {

    public static NoteExtendedDto fromEntity(
            Note note,
            List<ReminderDto> reminders,
            NoteSettingsDto settings,
            List<String> labels) {
        return new NoteExtendedDto(
                note.getId(),
                note.getTitle(),
                note.getContent(),
                note.getType(),
                note.getCreationDate(),
                note.isPinned(),
                note.isArchived(),
                note.getColor(),
                note.getOwner(),
                reminders,
                settings,
                labels);
    }
}
