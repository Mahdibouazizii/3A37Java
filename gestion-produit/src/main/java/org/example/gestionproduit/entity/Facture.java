package org.example.gestionproduit.entity;

import java.sql.Timestamp;

public class






Facture {
    private int id;
    private int idCommande;    // Link to the Commande (order)
    private double total;      // Total amount of the facture
    private String details;    // Details of the items in the facture
    private Timestamp createdAt; // Date of facture creation

    // Constructor to initialize the Facture
    public Facture(int id, int idCommande, double total, String details, Timestamp createdAt) {
        this.id = id;
        this.idCommande = idCommande;
        this.total = total;
        this.details = details;
        this.createdAt = createdAt;
    }

    // Constructor for new Facture (id auto-generated)
    public Facture(int idCommande, double total, String details) {
        this(0, idCommande, total, details, new Timestamp(System.currentTimeMillis()));
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCommande() {
        return idCommande;
    }

    public void setIdCommande(int idCommande) {
        this.idCommande = idCommande;
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
}
