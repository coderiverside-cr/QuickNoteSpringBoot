package com.coderiverside.quicknote;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record NoteDto(
        long id,
        String title,
        String content,
        String type,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'") LocalDateTime creationDate,
        boolean isPinned,
        boolean isArchived,
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
        note.setId(this.id);
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
