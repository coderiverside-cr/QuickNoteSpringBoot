package com.coderiverside.quicknote;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coderiverside.quicknote.exception.BadRequestException;
import com.coderiverside.quicknote.exception.ResourceNotFoundException;

@Service
public class NoteSettingsService {

    private final NoteService noteService;
    private final NoteSettingsRepository noteSettingsRepository;

    public NoteSettingsService(NoteService noteService, NoteSettingsRepository settingsRepository) {
        this.noteService = noteService;
        this.noteSettingsRepository = settingsRepository;
    }

    @Transactional(readOnly = true)
    public NoteSettingsDto getNoteSetting(Long noteId, String owner) {
        noteService.getNoteById(noteId, owner);
        NoteSettings settings = getNoteSettingsById(noteId);
        return NoteSettingsDto.fromEntity(settings);
    }

    protected NoteSettings getNoteSettingsById(Long noteId) {
        if (noteId == null) {
            throw new BadRequestException("Note id cannot be null");
        }
        return noteSettingsRepository.findByNoteId(noteId)
                .orElseThrow(() -> new ResourceNotFoundException(noteId));
    }

    @Transactional
    public NoteSettingsDto create(Long noteId, NoteSettingsDto settingsDto, String owner) {
        if (settingsDto == null) {
            throw new BadRequestException("Settings cannot be null");
        }

        Note note = noteService.getNoteById(noteId, owner);

        if (noteSettingsRepository.findByNoteId(noteId).isPresent()) {
            throw new BadRequestException("Settings already exist for note " + noteId);
        }

        NoteSettings settings = settingsDto.toEntity(note);
        settings = noteSettingsRepository.save(settings);

        return NoteSettingsDto.fromEntity(settings);
    }

    @Transactional
    public NoteSettingsDto update(Long noteId, NoteSettingsDto settingsDto, String owner) {
        if (noteId == null) {
            throw new BadRequestException("Note id cannot be null");
        }
        if (settingsDto == null) {
            throw new BadRequestException("Settings cannot be null");
        }

        Note note = noteService.getNoteById(noteId, owner);
        NoteSettings existingSettings = getNoteSettingsById(noteId);
        NoteSettings newSettings = settingsDto.toEntity(note);

        existingSettings.setNote(note);
        existingSettings.setLocked(newSettings.isLocked());
        existingSettings.setPriority(newSettings.getPriority());
        existingSettings.setEnableSharing(newSettings.isEnableSharing());

        existingSettings = noteSettingsRepository.save(existingSettings);
        return NoteSettingsDto.fromEntity(existingSettings);
    }

    @Transactional
    public void deleteByNoteId(Long noteId, String owner) {
        noteService.getNoteById(noteId, owner);
        getNoteSettingsById(noteId);
        // En este modelo NoteSettings solo tiene referencia a Note, así que solo se
        // borra el NoteSettings
        noteSettingsRepository.deleteByNoteId(noteId);
    }
}
