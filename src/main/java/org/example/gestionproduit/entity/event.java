package org.example.gestionproduit.entity;

import java.util.Date;

public class event {
    private int id; // Auto-incrémenté dans la base
    private String nom;
    private String description;
    private Date heur_debut;
    private Date heur_fin;
    private String image;
    private int nbrplacetottale;

    // ✅ Constructeur à utiliser pour ajouter un event dans la base (sans ID)
    public event(String nom, String description, Date heur_debut, Date heur_fin, String image, int nbrplacetottale) {
        this.nom = nom;
        this.description = description;
        this.heur_debut = heur_debut;
        this.heur_fin = heur_fin;
        this.image = image;
        this.nbrplacetottale = nbrplacetottale;
    }

    // ✅ Constructeur complet (utilisé pour lire un event avec ID depuis la base)
    public event(int id, String nom, String description, Date heur_debut, Date heur_fin, String image, int nbrplacetottale) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.heur_debut = heur_debut;
        this.heur_fin = heur_fin;
        this.image = image;
        this.nbrplacetottale = nbrplacetottale;
    }
    public event() {
    }
    // Getters & Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id; // ⚠️ À utiliser uniquement après insertion si tu récupères la clé générée
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getHeur_debut() {
        return heur_debut;
    }

    public void setHeur_debut(Date heur_debut) {
        this.heur_debut = heur_debut;
    }

    public Date getHeur_fin() {
        return heur_fin;
    }

    public void setHeur_fin(Date heur_fin) {
        this.heur_fin = heur_fin;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getNbrplacetottale() {
        return nbrplacetottale;
    }

    public void setNbrplacetottale(int nbrplacetottale) {
        this.nbrplacetottale = nbrplacetottale;
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", heur_debut=" + heur_debut +
                ", heur_fin=" + heur_fin +
                ", image='" + image + '\'' +
                ", nbrplacetottale=" + nbrplacetottale +
                '}';
    }
}
