package org.example.gestionproduit.entity;

import java.sql.Timestamp;

public class Facture {

    private int id;
    private int idCommande;        // Link to the Commande (order)
    private double total;          // Total amount of the facture
    private String details;        // Details of the items in the facture
    private Timestamp createdAt;   // Date of facture creation
    private String userNom;        // Client name to display on the facture
    private int idUser;            // User ID
    private String etatPaiement;   // Payment status: "payée" or "non payée"

    // ✅ Full constructor
    public Facture(int id, int idCommande, double total, String details, Timestamp createdAt, String userNom) {
        this.id = id;
        this.idCommande = idCommande;
        this.total = total;
        this.details = details;
        this.createdAt = createdAt;
        this.userNom = userNom;
    }

    // ✅ Constructor without ID (used for insertions)
    public Facture(int idCommande, double total, String details, String userNom) {
        this(0, idCommande, total, details, new Timestamp(System.currentTimeMillis()), userNom);
    }

    // ✅ Constructor for backward compatibility (no userNom provided)
    public Facture(int idCommande, double total, String details) {
        this(0, idCommande, total, details, new Timestamp(System.currentTimeMillis()), "");
    }

    // 🔽 Getters and Setters

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

    public String getUserNom() {
        return userNom;
    }

    public void setUserNom(String userNom) {
        this.userNom = userNom;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getEtatPaiement() {
        return etatPaiement;
    }

    public void setEtatPaiement(String etatPaiement) {
        this.etatPaiement = etatPaiement;
    }

    // ✅ Optional: for debugging or logging
    @Override
    public String toString() {
        return "Facture{" +
                "id=" + id +
                ", idCommande=" + idCommande +
                ", total=" + total +
                ", details='" + details + '\'' +
                ", createdAt=" + createdAt +
                ", userNom='" + userNom + '\'' +
                ", idUser=" + idUser +
                ", etatPaiement='" + etatPaiement + '\'' +
                '}';
    }
}
