package com.coderiverside.quicknote;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notes")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    private String title;
    private String content;
    private String type;
    
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    
    @Column(name = "is_pinned")
    private boolean isPinned;
    
    @Column(name = "is_archived")
    private boolean isArchived;
    
    private String color;

    public Note() {}

    public Note(long id, String title, String content, String type, LocalDateTime creationDate, boolean isPinned, boolean isArchived, String color) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.type = type;
        this.creationDate = creationDate;
        this.isPinned = isPinned;
        this.isArchived = isArchived;
        this.color = color;
    }
    
    // Getters y setters (necesarios para la mutabilidad)

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }

    public boolean isArchived() {
        return isArchived;
    }

    public void setArchived(boolean archived) {
        isArchived = archived;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}