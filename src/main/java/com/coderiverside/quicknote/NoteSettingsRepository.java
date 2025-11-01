package com.coderiverside.quicknote;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteSettingsRepository extends JpaRepository<NoteSettings, Long> {
    Optional<NoteSettings> findByNoteId(Long noteId);

    void deleteByNoteId(Long noteId);
}
