package org.example.gestionproduit.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.example.gestionproduit.entity.Commande;
import org.example.gestionproduit.service.CommandeService;
import org.example.gestionproduit.session.UserSession;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MesCommandesController {

    @FXML
    private FlowPane flowPane;

    @FXML
    public void initialize() {
        List<Commande> commandes = getMyCommandes();
        for (Commande cmd : commandes) {
            flowPane.getChildren().add(createCommandeCard(cmd));
        }
    }

    private List<Commande> getMyCommandes() {
        List<Commande> list = new ArrayList<>();
        int userId = UserSession.getInstance().getUser().getId();

        String query = "SELECT * FROM commande WHERE id_user = ? ORDER BY created_at DESC";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/projetpi2", "root", "");
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Commande cmd = new Commande(
                        rs.getInt("id"),
                        rs.getDouble("total"),
                        rs.getString("details"),
                        rs.getTimestamp("created_at"),
                        userId,
                        rs.getString("adresse"),
                        rs.getString("type_paiement"),
                        rs.getString("status"),
                        null
                );
                list.add(cmd);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private VBox createCommandeCard(Commande commande) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ccc; -fx-border-radius: 8; -fx-background-radius: 8;");
        card.setPrefWidth(300);

        Label total = new Label("💶 Total: " + commande.getTotal() + " €");
        Label details = new Label("🧾 Détails: " + commande.getDetails());
        Label date = new Label("📅 Date: " + commande.getCreatedAt());
        Label adresse = new Label("📍 Adresse: " + (commande.getAdresse() != null ? commande.getAdresse() : "—"));
        Label paiement = new Label("💳 Paiement: " + (commande.getTypePaiement() != null ? commande.getTypePaiement() : "—"));
        Label status = new Label("📦 Statut: " + (commande.getStatus() != null ? commande.getStatus() : "—"));

        card.getChildren().addAll(total, details, date, adresse, paiement, status);
        return card;
    }
}
