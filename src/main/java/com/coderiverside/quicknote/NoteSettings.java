package com.coderiverside.quicknote;

import jakarta.persistence.*;

@Entity
@Table(name = "note_settings")
public class NoteSettings {

    @Id
    @Column(name = "note_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId 
    @JoinColumn(name = "note_id", nullable = false) 
    private Note note;

    @Column(name = "is_locked", nullable = false)
    private boolean isLocked = false; 

    @Column(length = 20)
    private String priority = "Low"; 

    @Column(name = "enable_sharing", nullable = false)
    private boolean enableSharing = true; 

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean isEnableSharing() {
        return enableSharing;
    }

    public void setEnableSharing(boolean enableSharing) {
        this.enableSharing = enableSharing;
    }
}