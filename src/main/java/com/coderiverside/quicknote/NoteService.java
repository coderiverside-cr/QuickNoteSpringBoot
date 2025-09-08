package com.coderiverside.quicknote;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class NoteService {

    private final NoteRepository noteRepository;

    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    public Optional<Note> getNoteById(Long id, String owner) {
        if (id == null) {
            return Optional.empty();
        }
        return noteRepository.findByIdAndOwner(id, owner);
    }

    public Note save(Note note) {
        return noteRepository.save(note);
    }

    public List<Note> getAllNotes(Pageable pageable, String owner) {
        return noteRepository.findAllByOwner(owner,
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        //Sort.by(Sort.Direction.DESC, "title")  
                        pageable.getSort()
                        ))
                .getContent();
    }

    public void delete(long noteId) {       
        noteRepository.deleteById(noteId);
    }

}
