package com.coderiverside.quicknote;

import java.time.LocalDateTime;

public record ReminderDto(
        Long id,
        LocalDateTime remindAt,
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

    public Reminder toEntity(String owner) {
        Reminder reminder = new Reminder();
        reminder.setRemindAt(this.remindAt);
        reminder.setOwner(owner);
        return reminder;
    }
}
