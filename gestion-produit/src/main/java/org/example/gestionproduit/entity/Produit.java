package org.example.gestionproduit.entity;

import javafx.beans.property.*;

public class Produit {
    private final IntegerProperty id;
    private final StringProperty nom;
    private final StringProperty description;
    private final StringProperty image;
    private final DoubleProperty prix;
    private final IntegerProperty stock;
    private final DoubleProperty poids;

    public Produit(int id, String nom, String description, String image, double prix, int stock, double poids) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.description = new SimpleStringProperty(description);
        this.image = new SimpleStringProperty(image);
        this.prix = new SimpleDoubleProperty(prix);
        this.stock = new SimpleIntegerProperty(stock);
        this.poids = new SimpleDoubleProperty(poids);
    }

    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getNom() { return nom.get(); }
    public void setNom(String nom) { this.nom.set(nom); }
    public StringProperty nomProperty() { return nom; }

    public String getDescription() { return description.get(); }
    public void setDescription(String description) { this.description.set(description); }
    public StringProperty descriptionProperty() { return description; }

    public String getImage() { return image.get(); }
    public void setImage(String image) { this.image.set(image); }
    public StringProperty imageProperty() { return image; }

    public double getPrix() { return prix.get(); }
    public void setPrix(double prix) { this.prix.set(prix); }
    public DoubleProperty prixProperty() { return prix; }

    public int getStock() { return stock.get(); }
    public void setStock(int stock) { this.stock.set(stock); }
    public IntegerProperty stockProperty() { return stock; }

    public double getPoids() { return poids.get(); }
    public void setPoids(double poids) { this.poids.set(poids); }
    public DoubleProperty poidsProperty() { return poids; }
}
