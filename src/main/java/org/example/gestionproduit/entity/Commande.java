package org.example.gestionproduit.entity;

import java.sql.Timestamp;
import java.util.List;

public class Commande {
    private int id;
    private double total;
    private String details;    // e.g., list of product names and quantities (e.g., productId:quantity)
    private Timestamp createdAt;
    private int id_user; // This will store the user ID (the user who placed the order)

    // Constructor to initialize the Commande
    public Commande(int id, double total, String details, Timestamp createdAt, int id_user) {
        this.id = id;
        this.total = total;
        this.details = details;
        this.createdAt = createdAt;
        this.id_user = id_user;
    }

    // Constructor for new Commande (id auto-generated)
    public Commande(double total, String details, int id_user) {
        this(0, total, details, new Timestamp(System.currentTimeMillis()), id_user);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public int getIdUser() {
        return id_user;
    }

    public void setIdUser(int id_user) {
        this.id_user = id_user;
    }
}
