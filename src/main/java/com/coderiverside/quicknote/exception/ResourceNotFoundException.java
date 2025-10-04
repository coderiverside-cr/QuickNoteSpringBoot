package com.coderiverside.quicknote.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(Long id) {
        super("Note not found with id: " + id);
    }
}