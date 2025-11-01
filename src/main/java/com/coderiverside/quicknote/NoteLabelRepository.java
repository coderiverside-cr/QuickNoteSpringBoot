package com.coderiverside.quicknote;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteLabelRepository extends JpaRepository<NoteLabel, NoteLabelId> {
    List<NoteLabel> findAllByIdNoteId(Long noteId);

    Optional<NoteLabel> findByIdNoteIdAndIdLabelId(Long noteId, Long labelId);

    void deleteByIdNoteIdAndIdLabelId(Long noteId, Long labelId);
}
