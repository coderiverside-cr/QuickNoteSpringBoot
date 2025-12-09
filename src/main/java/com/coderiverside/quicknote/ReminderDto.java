package com.coderiverside.quicknote;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;

public record ReminderDto(
        Long id,
        @NotNull(message = "Reminder date is required") @Future(message = "Reminder date must be in the future") LocalDateTime remindAt,
        String owner) {

    public static ReminderDto fromEntity(Reminder reminder) {
        if (reminder == null) {
            return null;
        }
        return new ReminderDto(
                reminder.getId(),
                reminder.getRemindAt(),
                reminder.getOwner());
    }

    public Reminder toEntity(String owner, Note note) {
        Reminder reminder = new Reminder();
        reminder.setRemindAt(this.remindAt);
        reminder.setOwner(owner);
        reminder.setNote(note);
        return reminder;
    }
}
