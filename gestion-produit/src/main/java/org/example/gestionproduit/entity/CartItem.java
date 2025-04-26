package org.example.gestionproduit.entity;

import javafx.beans.property.*;
public class CartItem {
    private Produit produit;
    private int quantity;

    public CartItem(Produit produit, int quantity) {
        this.produit = produit;
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return produit.getPrix() * quantity;  // Recalculate total whenever quantity is updated
    }

    public Produit getProduit() {
        return produit;
    }
}




