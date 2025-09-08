package com.coderiverside.quicknote;


import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long>{
    Optional<Note> findByIdAndOwner(long id, String owner);    
    Page<Note> findAllByOwner(String owner, PageRequest pageRequest);
}
