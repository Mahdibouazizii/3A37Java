package org.example.gestionproduit.entity;

public class Feedback {
    private int id;
    private int produitId;
    private String commentaire;
    private int rating;
    private int userId;  // Adding userId to associate feedback with a specific user

    // Constructor for creating a new Feedback object with userId
    public Feedback(int id, int produitId, String commentaire, int rating, int userId) {
        this.id = id;
        this.produitId = produitId;
        this.commentaire = commentaire;
        this.rating = rating;
        this.userId = userId;
    }

    // Constructor for creating a new Feedback object without id (id will be auto-generated)
    public Feedback(int produitId, String commentaire, int rating, int userId) {
        this(0, produitId, commentaire, rating, userId);  // Calling the other constructor
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return produitId;
    }

    public void setProductId(int produitId) {
        this.produitId = produitId;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
