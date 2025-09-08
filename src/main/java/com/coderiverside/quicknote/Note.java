package com.coderiverside.quicknote;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
    private String owner;
    
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
    public void setPinned(boolean isPinned) {
        this.isPinned = isPinned;
    }
    public boolean isArchived() {
        return isArchived;
    }
    public void setArchived(boolean isArchived) {
        this.isArchived = isArchived;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    

}
