package com.coderiverside.quicknote;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.apache.catalina.connector.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class NoteService {
    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Optional<Note> getNoteById(Long noteId) {
        if (noteId == null) {
            return Optional.empty();
        }
        return noteRepository.findById(noteId);
    }

    public Note save(Note note) {
        return noteRepository.save(note);
    }

    public List<Note> getAllNotes(Pageable pageable) {
        Page<Note> page = noteRepository.findAll(
            PageRequest.of(
                pageable.getPageNumber(), 
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "Title")
            )
        );
        return page.getContent();
    }
}
