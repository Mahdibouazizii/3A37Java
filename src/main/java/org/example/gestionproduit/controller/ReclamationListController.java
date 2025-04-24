package org.example.gestionproduit.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.example.gestionproduit.entity.Reclamation;
import org.example.gestionproduit.service.ReclamationService;

import java.util.List;

public class ReclamationListController {

    @FXML
    private FlowPane reclamationContainer;

    private final ReclamationService reclamationService = new ReclamationService();

    @FXML
    public void initialize() {
        List<Reclamation> reclamations = reclamationService.getAllReclamations();
        for (Reclamation r : reclamations) {
            reclamationContainer.getChildren().add(createCard(r));
        }
    }

    private VBox createCard(Reclamation r) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setPrefWidth(300);
        card.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-radius: 5;");

        Label userIdLabel = new Label("👤 User ID: " + r.getUserId());
        Label sujetLabel = new Label("📌 Sujet: " + r.getSujet());
        Label descriptionLabel = new Label("📝 " + r.getDescription());
        Label createdAtLabel = new Label("🕒 " + r.getCreatedAt().toString());

        card.getChildren().addAll(userIdLabel, sujetLabel, descriptionLabel, createdAtLabel);
        return card;
    }
}
