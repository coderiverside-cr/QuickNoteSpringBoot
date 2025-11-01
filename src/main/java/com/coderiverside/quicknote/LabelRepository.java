package com.coderiverside.quicknote;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelRepository extends JpaRepository<Label, Long> {
    //Optional<Label> findByName(String name);

    Optional<Label> findByNameAndOwner(String name, String owner);

    Optional<Label> findByIdAndOwner(long id, String owner);    
    Page<Label> findAllByOwner(String owner, PageRequest pageRequest);

}
