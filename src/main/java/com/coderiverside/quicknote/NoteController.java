package com.coderiverside.quicknote;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.hibernate.query.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;



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
    public ResponseEntity<Void> createNote(@RequestBody NoteDto noteDto, UriComponentsBuilder ucb) {

        Note note = noteDto.toEntity();
        note = noteService.save(note);
        URI locationOfNote = ucb.path("/notes/{id}")
                .buildAndExpand(note.getId())
                .toUri();
        return ResponseEntity.created(locationOfNote).build();                 
        
    }
    
    @GetMapping("")
    public ResponseEntity<List<NoteDto>> getNotes(Pageable pageable){
        List<Note> notes = noteService.getAllNotes(pageable);
        List<NoteDto> noteDtos = notes.stream()
                .map(NoteDto::fromEntity)
                .toList();
        return ResponseEntity.ok(noteDtos);        
    }
    


}
