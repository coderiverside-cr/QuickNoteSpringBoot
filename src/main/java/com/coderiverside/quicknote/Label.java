package com.coderiverside.quicknote;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "labels")
public class Label {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Changed to Long for null safety

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String owner;

    // Required by JPA
    protected Label() {
    }

    // Constructor for new labels
    public Label(String name, String owner) {
        this.name = name;
        this.owner = owner;
    }

    public Long getId() { // Changed return type to Long
        return id;
    }

    protected void setId(Long id) { // Protected for JPA
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { // Kept public for updates
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    protected void setOwner(String owner) { // Protected for JPA
        this.owner = owner;
    }

}
