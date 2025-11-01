package com.coderiverside.quicknote;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "notes_labels")
public final class NoteLabel {

    @EmbeddedId
    private NoteLabelId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("noteId")
    @JoinColumn(name = "note_id", nullable = false)
    private Note note;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("labelId")
    @JoinColumn(name = "label_id", nullable = false)
    private Label label;

    protected NoteLabel() {
        // JPA
    }

    public NoteLabel(Note note, Label label) {
        this.note = note;
        this.label = label;
        this.id = new NoteLabelId(note.getId(), label.getId());
    }

    public NoteLabelId getId() {
        return id;
    }

    public Note getNote() {
        return note;
    }

    public Label getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NoteLabel other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "NoteLabel[id=" + id + "]";
    }
}
