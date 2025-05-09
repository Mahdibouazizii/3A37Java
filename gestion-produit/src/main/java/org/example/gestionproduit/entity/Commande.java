package org.example.gestionproduit.entity;

import java.sql.Timestamp;

public class Commande {
    private int id;
    private double total;
    private String details;
    private Timestamp createdAt;
    private int id_user;
    private String adresse;           // 🆕 nullable
    private String typePaiement;      // 🆕 nullable
    private String status;            // 🆕 nullable
    private String userNom;           // 🆕 for display only (not in DB directly)

    // Constructor with all fields
    public Commande(int id, double total, String details, Timestamp createdAt, int id_user,
                    String adresse, String typePaiement, String status, String userNom) {
        this.id = id;
        this.total = total;
        this.details = details;
        this.createdAt = createdAt;
        this.id_user = id_user;
        this.adresse = adresse;
        this.typePaiement = typePaiement;
        this.status = status;
        this.userNom = userNom;
    }

    // Constructor without userNom
    public Commande(int id, double total, String details, Timestamp createdAt, int id_user,
                    String adresse, String typePaiement, String status) {
        this(id, total, details, createdAt, id_user, adresse, typePaiement, status, null);
    }

    // Basic constructor (for inserts)
    public Commande(double total, String details, int id_user) {
        this(0, total, details, new Timestamp(System.currentTimeMillis()), id_user, null, null, null, null);
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public int getIdUser() { return id_user; }
    public void setIdUser(int id_user) { this.id_user = id_user; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getTypePaiement() { return typePaiement; }
    public void setTypePaiement(String typePaiement) { this.typePaiement = typePaiement; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getUserNom() { return userNom; }
    public void setUserNom(String userNom) { this.userNom = userNom; }
}
