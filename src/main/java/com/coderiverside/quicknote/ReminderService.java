package com.coderiverside.quicknote;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coderiverside.quicknote.exception.BadRequestException;
import com.coderiverside.quicknote.exception.ResourceNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReminderService {

    private final ReminderRepository reminderRepository;
    private final NoteService noteService;

    public ReminderService(ReminderRepository reminderRepository, NoteService noteService) {
        this.reminderRepository = reminderRepository;
        this.noteService = noteService;
    }

    @Transactional(readOnly = true)
    public List<ReminderDto> getAllReminders(
            Long noteId,
            String owner,
            Pageable pageable) {
        noteService.getNoteById(noteId, owner);
        return reminderRepository.findAllByNoteIdAndOwner(
                noteId,
                owner,
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSort()))
                .getContent()
                .stream()
                .map(ReminderDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReminderDto getReminderById(Long noteId, Long reminderId, String owner) {
        noteService.getNoteById(noteId, owner);
        Reminder reminder = getReminderById(reminderId, owner);
        return ReminderDto.fromEntity(reminder);
    }

    protected Reminder getReminderById(Long reminderId, String owner) {
        if (reminderId == null) {
            throw new BadRequestException("Reminder ID cannot be null");
        }
        return reminderRepository.findByIdAndOwner(reminderId, owner)
                .orElseThrow(() -> new ResourceNotFoundException(reminderId));
    }

    @Transactional
    public ReminderDto create(Long noteId, ReminderDto reminderDto, String owner) {
        if (reminderDto == null) {
            throw new BadRequestException("Reminder payload cannot be null");
        }
        if (reminderDto.remindAt() == null) {
            throw new BadRequestException("RemindAt cannot be null");
        }

        Note note = noteService.getNoteById(noteId, owner);
        Reminder reminder = reminderDto.toEntity(owner, note);
        reminder = reminderRepository.save(reminder);

        return ReminderDto.fromEntity(reminder);
    }

    @Transactional
    public ReminderDto update(Long noteId, Long reminderId, ReminderDto reminderDto, String owner) {
        if (reminderDto == null) {
            throw new BadRequestException("Reminder payload cannot be null");
        }
        if (reminderDto.remindAt() == null) {
            throw new BadRequestException("RemindAt cannot be null");
        }
        noteService.getNoteById(noteId, owner);

        Reminder existingReminder = getReminderById(reminderId, owner);
        existingReminder.setRemindAt(reminderDto.remindAt());

        existingReminder = reminderRepository.save(existingReminder);
        return ReminderDto.fromEntity(existingReminder);
    }

    @Transactional
    public void delete(Long noteId, Long reminderId, String owner) {
        noteService.getNoteById(noteId, owner);
        Reminder existingReminder = getReminderById(reminderId, owner);
        // Borrar asociaciones en Note si aplica (si hay una relación directa)
        // En este modelo Reminder solo tiene referencia a Note, así que solo se borra
        // el Reminder
        reminderRepository.delete(existingReminder);
    }
}
