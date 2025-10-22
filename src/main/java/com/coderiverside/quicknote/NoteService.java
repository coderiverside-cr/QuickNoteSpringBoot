package com.coderiverside.quicknote;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.coderiverside.quicknote.exception.BadRequestException;
import com.coderiverside.quicknote.exception.ResourceNotFoundException;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Note getNoteById(Long id, String owner) {
        if (id == null) {
            throw new BadRequestException("Note id cannot be null");
        }
        return noteRepository.findByIdAndOwner(id, owner)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    public Note save(Note note) {
        if (note == null) {
            throw new BadRequestException("Note cannot be null");
        }
        if (note.getTitle() == null || note.getTitle().trim().isEmpty()) {
            throw new BadRequestException("Note title cannot be empty");
        }
        if (note.getContent() == null || note.getContent().trim().isEmpty()) {
            throw new BadRequestException("Note content cannot be empty");
        }
        return noteRepository.save(note);
    }

    public Note update(Long id, Note note, String owner) {
        if (note == null) {
            throw new BadRequestException("Note cannot be null");
        }
        if (id == null) {
            throw new BadRequestException("Note id cannot be null");
        }

        Note existingNote = getNoteById(id, owner);
        existingNote.setTitle(note.getTitle());
        existingNote.setContent(note.getContent());
        existingNote.setType(note.getType());
        existingNote.setPinned(note.isPinned());
        existingNote.setArchived(note.isArchived());
        existingNote.setColor(note.getColor());

        return save(existingNote);
    }

    public List<Note> getAllNotes(Pageable pageable, String owner) {
        if (pageable == null) {
            throw new BadRequestException("Pageable cannot be null");
        }

        return noteRepository.findAllByOwner(owner,
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        // Sort.by(Sort.Direction.DESC, "title")
                        pageable.getSort()))
                .getContent();
    }

    public void delete(long noteId, String owner) {

        Note note = getNoteById(noteId, owner);
        noteRepository.delete(note);
    }

}
