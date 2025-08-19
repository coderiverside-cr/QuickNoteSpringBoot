package com.coderiverside.quicknote;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.hibernate.query.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;




@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }
    
    @GetMapping("/{noteId}")
    private ResponseEntity<NoteDto> getNoteById(@PathVariable Long noteId){

        Optional<Note> noteOptional = noteService.getNoteById(noteId);
        if(noteOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Note note = noteOptional.get();
        NoteDto noteDto = NoteDto.fromEntity(note);
        return ResponseEntity.ok(noteDto);
    }

    @PostMapping("")
    private ResponseEntity<Void> createNote(@RequestBody NoteDto noteDto, UriComponentsBuilder ucb) {

        Note note = noteDto.toEntity();
        note = noteService.save(note);
        URI locationOfNote = ucb.path("/notes/{id}")
                .buildAndExpand(note.getId())
                .toUri();
        return ResponseEntity.created(locationOfNote).build();                 
        
    }
    
    @GetMapping("")
    private ResponseEntity<List<NoteDto>> getNotes(Pageable pageable){
        List<Note> notes = noteService.getAllNotes(pageable);
        List<NoteDto> noteDtos = notes.stream()
                .map(NoteDto::fromEntity)
                .toList();
        return ResponseEntity.ok(noteDtos);        
    }
    
    @PutMapping("/{noteId}")
    private ResponseEntity<Void> updateNote(@PathVariable Long noteId, @RequestBody NoteDto noteDto) {
        Optional<Note> existingNoteOptional = noteService.getNoteById(noteId);
        if (existingNoteOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Note existingNote = existingNoteOptional.get();
        Note updatedNote = noteDto.toEntity();
        updatedNote.setId(existingNote.getId());
        
        noteService.save(updatedNote);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{noteId}")
    private ResponseEntity<Void> deleteNote(@PathVariable Long noteId) {
        Optional<Note> existingNoteOptional = noteService.getNoteById(noteId);
        if (existingNoteOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        noteService.delete(noteId);
        return ResponseEntity.noContent().build();
    }


}
