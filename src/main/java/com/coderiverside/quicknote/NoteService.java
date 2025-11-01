package com.coderiverside.quicknote;

import java.util.List;

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

    public NoteService(NoteRepository noteRepository,
            ReminderRepository reminderRepository,
            NoteLabelRepository noteLabelRepository,
            NoteSettingsRepository noteSettingsRepository) {
        this.noteRepository = noteRepository;
        this.reminderRepository = reminderRepository;
        this.noteLabelRepository = noteLabelRepository;
        this.noteSettingsRepository = noteSettingsRepository;
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

    @Transactional
    public NoteDto createNote(NoteDto noteDto, String owner) {
        validateNoteDto(noteDto);
        Note note = noteDto.toEntity(owner);
        Note saved = noteRepository.save(note);
        return getNoteByIdDto(saved.getId(), owner);
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
