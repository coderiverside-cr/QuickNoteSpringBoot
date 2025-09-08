package com.coderiverside.quicknote;

import java.net.URI;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/{id}")
    private ResponseEntity<NoteDto> getNoteById(
            @PathVariable Long id,
            Principal principal) {
        Optional<Note> noteOptional = noteService.getNoteById(id, principal.getName());
        if (noteOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Note note = noteOptional.get();
        NoteDto noteDto = NoteDto.fromEntity(note);
        return ResponseEntity.ok(noteDto);
    }

    @PostMapping("")
    private ResponseEntity<Void> createNote(
            @RequestBody NoteDto noteDto,
            UriComponentsBuilder ucb,
            Principal principal) {

        Note note = noteDto.toEntity(principal.getName());
        note = noteService.save(note);
        URI location = ucb.path("/notes/{id}")
                .buildAndExpand(note.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("")
    private ResponseEntity<List<NoteDto>> getAllNotes(
        Principal principal,
        Pageable pageable) {
        List<Note> notes = noteService.getAllNotes(pageable, principal.getName());
        List<NoteDto> noteDtos = notes.stream()
                .map(NoteDto::fromEntity)
                .toList();
        return ResponseEntity.ok(noteDtos);
    }

    @PutMapping("/{id}")
    private ResponseEntity<Void> updateNote(
            @PathVariable Long id,
            @RequestBody NoteDto noteDto,
            Principal principal) {
        Optional<Note> noteOptional = noteService.getNoteById(id, principal.getName());
        if (noteOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Note note = noteOptional.get();
        Note updatedNote = noteDto.toEntity(principal.getName());
        updatedNote.setId(note.getId());
        noteService.save(updatedNote);
        return ResponseEntity.noContent().build();

    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Void> deleteNote(@PathVariable Long id,
            Principal principal) {
        Optional<Note> noteOptional = noteService.getNoteById(id, principal.getName());
        if (noteOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        noteService.delete(id);
        return ResponseEntity.noContent().build();

    }

}
