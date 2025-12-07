package com.coderiverside.quicknote;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * DTO que representa una nota completa con todas sus relaciones.
 * Se usa tanto para entrada (creación) como para salida (lectura de relaciones).
 */
public record NoteExtendedDto(
        Long id,
        String title,
        String content,
        String type,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") LocalDateTime creationDate,
        boolean isPinned,
        boolean isArchived,
        String color,
        String owner,
        List<ReminderDto> reminders,
        NoteSettingsDto settings,
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
