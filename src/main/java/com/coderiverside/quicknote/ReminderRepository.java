package com.coderiverside.quicknote;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    Page<Reminder> findAllByNoteIdAndOwner(Long noteId, String owner, PageRequest pageRequest);

    Optional<Reminder> findByIdAndOwner(Long id, String owner);

}
