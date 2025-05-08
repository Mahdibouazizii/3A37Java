package org.example.gestionproduit.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.example.gestionproduit.entity.Commande;
import org.example.gestionproduit.service.CommandeService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeListController {

    @FXML
    private FlowPane cardContainer;

    private final CommandeService commandeService = new CommandeService();

    @FXML
    public void initialize() {
        refreshCommandeList(); // Load the cards on init
    }

    private void refreshCommandeList() {
        cardContainer.getChildren().clear();
        List<Commande> commandes = getAllCommandesWithUserNom();
        for (Commande cmd : commandes) {
            cardContainer.getChildren().add(createCommandeCard(cmd));
        }
    }

    private Pane createCommandeCard(Commande commande) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; -fx-background-radius: 8; -fx-border-radius: 8;");
        card.setPrefWidth(300);

        Label user = new Label("👤 Client: " + (commande.getUserNom() != null ? commande.getUserNom() : "Inconnu"));
        Label total = new Label("💶 Total: " + commande.getTotal() + " €");
        Text details = new Text("📄 Détails: " + commande.getDetails());
        details.setWrappingWidth(280);
        Label createdAt = new Label("🕒 Date: " + commande.getCreatedAt());
        Label adresse = new Label("📍 Adresse: " + (commande.getAdresse() != null ? commande.getAdresse() : "—"));
        Label paiement = new Label("💳 Paiement: " + (commande.getTypePaiement() != null ? commande.getTypePaiement() : "—"));
        Label status = new Label("📦 Statut: " + (commande.getStatus() != null ? commande.getStatus() : "—"));

        Button validateBtn = new Button("✅ Valider");
        validateBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

        if (!"confirmé".equalsIgnoreCase(commande.getStatus())) {
            validateBtn.setOnAction(e -> {
                boolean success = commandeService.validateCommande(commande.getId());
                if (success) {
                    refreshCommandeList(); // 💥 refresh all cards
                }
            });
        } else {
            validateBtn.setDisable(true);
        }

        card.getChildren().addAll(user, total, details, createdAt, adresse, paiement, status, validateBtn);
        return card;
    }

    private List<Commande> getAllCommandesWithUserNom() {
        List<Commande> list = new ArrayList<>();
        String query = """
                SELECT c.*, u.nom AS user_nom 
                FROM commande c
                LEFT JOIN user u ON c.id_user = u.id
                ORDER BY c.created_at DESC
                """;

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/projetpi2", "root", "");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Commande cmd = new Commande(
                        rs.getInt("id"),
                        rs.getDouble("total"),
                        rs.getString("details"),
                        rs.getTimestamp("created_at"),
                        rs.getInt("id_user"),
                        rs.getString("adresse"),
                        rs.getString("type_paiement"),
                        rs.getString("status"),
                        rs.getString("user_nom")
                );
                list.add(cmd);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
