package com.coderiverside.quicknote;

import java.net.URI;
import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
        public ResponseEntity<NoteDto> getNoteById(
                        @PathVariable Long id,
                        Principal principal) {
                NoteDto note = noteService.getNoteByIdDto(id, principal.getName());
                return ResponseEntity.ok(note);
        }

        @GetMapping("/{id}/extended")
        public ResponseEntity<NoteExtendedDto> getNoteWithRelations(
                        @PathVariable Long id,
                        Principal principal) {
                NoteExtendedDto note = noteService.getNoteWithRelationsDto(id, principal.getName());
                return ResponseEntity.ok(note);
        }

        @PostMapping("")
        public ResponseEntity<Void> createNote(
                        @RequestBody NoteDto noteDto,
                        UriComponentsBuilder ucb,
                        Principal principal) {
                NoteDto created = noteService.createNote(noteDto, principal.getName());
                URI location = ucb.path("/notes/{id}")
                                .buildAndExpand(created.id())
                                .toUri();
                return ResponseEntity.created(location).build();
        }

        @PostMapping("/extended")
        public ResponseEntity<NoteDto> createFullNote(
                        @RequestBody NoteExtendedDto fullNoteDto,
                        UriComponentsBuilder ucb,
                        Principal principal) {
                NoteDto created = noteService.createFullNote(fullNoteDto, principal.getName());
                URI location = ucb.path("/notes/{id}")
                                .buildAndExpand(created.id())
                                .toUri();
                return ResponseEntity.created(location).body(created);
        }

        @GetMapping("")
        public ResponseEntity<List<NoteDto>> getAllNotes(
                        Principal principal,
                        Pageable pageable) {
                List<NoteDto> notes = noteService.getAllNotes(pageable, principal.getName());
                return ResponseEntity.ok(notes);
        }

        @PutMapping("/{id}")
        public ResponseEntity<Void> updateNote(
                        @PathVariable Long id,
                        @RequestBody NoteDto noteDto,
                        Principal principal) {
                noteService.updateNote(id, noteDto, principal.getName());
                return ResponseEntity.noContent().build();
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteNote(
                        @PathVariable Long id,
                        Principal principal) {
                noteService.deleteNote(id, principal.getName());
                return ResponseEntity.noContent().build();
        }

}
