package com.coderiverside.quicknote;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coderiverside.quicknote.exception.BadRequestException;
import com.coderiverside.quicknote.exception.ResourceNotFoundException;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final ReminderRepository reminderRepository;
    private final NoteLabelRepository noteLabelRepository;
    private final NoteSettingsRepository noteSettingsRepository;
    private final LabelRepository labelRepository;

    public NoteService(NoteRepository noteRepository,
            ReminderRepository reminderRepository,
            NoteLabelRepository noteLabelRepository,
            NoteSettingsRepository noteSettingsRepository,
            LabelRepository labelRepository) {
        this.noteRepository = noteRepository;
        this.reminderRepository = reminderRepository;
        this.noteLabelRepository = noteLabelRepository;
        this.noteSettingsRepository = noteSettingsRepository;
        this.labelRepository = labelRepository;
    }

    protected Note getNoteById(Long id, String owner) {
        if (id == null) {
            throw new BadRequestException("Note id cannot be null");
        }
        return noteRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public NoteDto getNoteByIdDto(Long id, String owner) {
        Note note = getNoteById(id, owner);
        return NoteDto.fromEntity(note);
    }

    /**
     * Get a note with all its relations (reminders, settings, labels).
     * Useful for detailed views of a note.
     */
    @Transactional(readOnly = true)
    public NoteExtendedDto getNoteWithRelationsDto(Long id, String owner) {
        Note note = getNoteById(id, owner);

        // Fetch reminders
        List<ReminderDto> reminders = reminderRepository
                .findAllByNoteIdAndOwner(id, owner, PageRequest.of(0, 100))
                .getContent()
                .stream()
                .map(ReminderDto::fromEntity)
                .toList();

        // Fetch settings
        NoteSettingsDto settings = noteSettingsRepository
                .findByNoteId(id)
                .map(NoteSettingsDto::fromEntity)
                .orElse(null);

        // Fetch labels
        List<String> labels = noteLabelRepository
                .findAllByIdNoteId(id)
                .stream()
                .map(NoteLabel::getLabel)
                .map(Label::getName)
                .toList();

        return NoteExtendedDto.fromEntity(note, reminders, settings, labels);
    }

    @Transactional
    public NoteDto createNote(NoteDto noteDto, String owner) {
        validateNoteDto(noteDto);
        Note note = noteDto.toEntity(owner);
        Note saved = noteRepository.save(note);
        return getNoteByIdDto(saved.getId(), owner);
    }

    @Transactional
    public NoteDto createFullNote(NoteExtendedDto dto, String owner) {
        // Create and persist base note
        Note note = buildNoteFromDto(dto, owner);
        Note saved = noteRepository.save(note);

        // Persist settings if provided
        if (dto.settings() != null) {
            persistNoteSettings(saved, dto.settings());
        }

        // Persist reminders if provided (with validation)
        if (dto.reminders() != null && !dto.reminders().isEmpty()) {
            persistReminders(saved, dto.reminders(), owner);
        }

        // Persist labels if provided (deduplicated, with validation)
        if (dto.labels() != null && !dto.labels().isEmpty()) {
            persistLabels(saved, dto.labels(), owner);
        }

        return getNoteByIdDto(saved.getId(), owner);
    }

    /**
     * Build a Note entity from NoteExtendedDto, applying defaults and trimming
     * inputs.
     * Throws BadRequestException if title or content are invalid.
     */
    private Note buildNoteFromDto(NoteExtendedDto dto, String owner) {
        // Validate inputs
        if (dto.title() == null || dto.title().trim().isEmpty()) {
            throw new BadRequestException("Title cannot be empty");
        }
        if (dto.content() == null || dto.content().trim().isEmpty()) {
            throw new BadRequestException("Content cannot be empty");
        }

        Note note = new Note();
        note.setTitle(dto.title().trim());
        note.setContent(dto.content().trim());
        note.setType(dto.type());
        note.setPinned(dto.isPinned());
        note.setArchived(dto.isArchived());
        note.setColor(dto.color());
        note.setOwner(owner);
        note.setCreationDate(dto.creationDate() != null ? dto.creationDate() : LocalDateTime.now());
        return note;
    }

    /**
     * Persist NoteSettings associated with the given note.
     */
    private void persistNoteSettings(Note note, NoteSettingsDto settingsDto) {
        NoteSettings settings = settingsDto.toEntity(note);
        noteSettingsRepository.save(settings);
    }

    /**
     * Persist reminders associated with the given note.
     * Validates that remindAt is not null.
     */
    private void persistReminders(Note note, List<ReminderDto> reminderDtos, String owner) {
        reminderDtos.stream()
                .filter(rd -> rd.remindAt() != null)
                .map(rd -> rd.toEntity(owner, note))
                .forEach(reminderRepository::save);
    }

    /**
     * Persist labels associated with the given note, deduplicating label names.
     * Empty label names are ignored. Creates new labels if they don't exist.
     */
    private void persistLabels(Note note, List<String> labelNames, String owner) {
        // Deduplicate and validate label names
        Set<String> uniqueLabels = labelNames.stream()
                .filter(name -> name != null && !name.trim().isEmpty())
                .map(String::trim)
                .collect(java.util.stream.Collectors.toSet());

        if (uniqueLabels.size() > 50) {
            throw new BadRequestException("Cannot have more than 50 unique labels");
        }

        // Create NoteLabel associations
        uniqueLabels.stream()
                .map(name -> labelRepository.findByNameAndOwner(name, owner)
                        .orElseGet(() -> labelRepository.save(new Label(name, owner))))
                .map(label -> new NoteLabel(note, label))
                .forEach(noteLabelRepository::save);
    }

    public NoteDto updateNote(Long id, NoteDto noteDto, String owner) {
        validateNoteDto(noteDto);
        if (id == null) {
            throw new BadRequestException("Note id cannot be null");
        }

        Note existingNote = getNoteById(id, owner);
        updateNoteFromDto(existingNote, noteDto);
        Note updated = noteRepository.save(existingNote);
        return getNoteByIdDto(updated.getId(), owner);
    }

    public List<NoteDto> getAllNotes(Pageable pageable, String owner) {
        if (pageable == null) {
            throw new BadRequestException("Pageable cannot be null");
        }

        return noteRepository.findAllByOwner(owner,
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSort()))
                .getContent()
                .stream()
                .map(note -> getNoteByIdDto(note.getId(), owner))
                .toList();
    }

    @Transactional
    public void deleteNote(long noteId, String owner) {
        Note note = getNoteById(noteId, owner);
        reminderRepository.deleteAll(
                reminderRepository.findAllByNoteIdAndOwner(noteId, owner, PageRequest.of(0, 100)).getContent());
        noteLabelRepository.deleteAll(noteLabelRepository.findAllByIdNoteId(noteId));
        noteSettingsRepository.deleteByNoteId(noteId);
        noteRepository.delete(note);
    }

    // Helper methods
    private void validateNoteDto(NoteDto noteDto) {
        if (noteDto == null) {
            throw new BadRequestException("Note cannot be null");
        }
        if (noteDto.title() == null || noteDto.title().trim().isEmpty()) {
            throw new BadRequestException("Note title cannot be empty");
        }
        if (noteDto.content() == null || noteDto.content().trim().isEmpty()) {
            throw new BadRequestException("Note content cannot be empty");
        }
    }

    private void updateNoteFromDto(Note note, NoteDto dto) {
        note.setTitle(dto.title());
        note.setContent(dto.content());
        note.setType(dto.type());
        note.setPinned(dto.isPinned());
        note.setArchived(dto.isArchived());
        note.setColor(dto.color());
    }

}
