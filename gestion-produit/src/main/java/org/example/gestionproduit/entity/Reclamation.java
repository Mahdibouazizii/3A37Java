package org.example.gestionproduit.entity;

import java.sql.Timestamp;

public class Reclamation {
    private int id;
    private int userId;
    private String sujet;
    private String description;
    private Timestamp createdAt;

    public Reclamation() {}

    public Reclamation(int userId, String sujet, String description, Timestamp createdAt) {
        this.userId = userId;
        this.sujet = sujet;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Reclamation(int id, int userId, String sujet, String description, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.sujet = sujet;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Getters & Setters...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getSujet() { return sujet; }
    public void setSujet(String sujet) { this.sujet = sujet; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
