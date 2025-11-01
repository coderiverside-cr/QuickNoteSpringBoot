package com.coderiverside.quicknote;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

@Embeddable
public class NoteLabelId implements Serializable {
    private static final long serialVersionUID = 1L;

    private final Long noteId;
    private final Long labelId;

    protected NoteLabelId() {
        this.noteId = null;
        this.labelId = null;
    }

    public NoteLabelId(Long noteId, Long labelId) {
        this.noteId = noteId;
        this.labelId = labelId;
    }

    public Long getNoteId() {
        return noteId;
    }

    public Long getLabelId() {
        return labelId;
    }    
}
