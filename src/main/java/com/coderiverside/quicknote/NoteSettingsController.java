package com.coderiverside.quicknote;

import java.net.URI;
import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notes/{noteId}/settings")
public class NoteSettingsController {

    private final NoteSettingsService settingsService;

    public NoteSettingsController(NoteSettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping("")
    public ResponseEntity<NoteSettingsDto> getNoteSetting(
            @PathVariable Long noteId,
            Principal principal) {
        NoteSettingsDto settings = settingsService.getNoteSetting(
                noteId,
                principal.getName());
        return ResponseEntity.ok(settings);
    }

    @PostMapping("")
    public ResponseEntity<NoteSettingsDto> createSettings(
            @PathVariable Long noteId,
            @RequestBody NoteSettingsDto settingsDto,
            Principal principal) {

        NoteSettingsDto createdSettings = settingsService.create(noteId, settingsDto, principal.getName());
        URI location = URI.create(String.format("/notes/%d/settings", noteId));
        return ResponseEntity.created(location).body(createdSettings);
    }

    @PutMapping("")
    public ResponseEntity<Void> updateNoteSettings(
            @PathVariable Long noteId,
            @RequestBody NoteSettingsDto settingsDto,
            Principal principal) {
        settingsService.update(noteId, settingsDto, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("")
    public ResponseEntity<Void> deleteSettings(
            @PathVariable Long noteId,
            Principal principal) {
        settingsService.deleteByNoteId(noteId, principal.getName());
        return ResponseEntity.noContent().build();
    }

}
