package org.example.gestionproduit.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class DashboardController {

    @FXML
    private BorderPane mainPane; // Le conteneur principal où le contenu est remplacé

    @FXML
    private Button accueilButton;

    @FXML
    private Button evenementButton;

    @FXML
    private Button reservationButton;

    @FXML
    private Button profilButton;

    @FXML
    private Label bienvenueLabel;

    @FXML
    private void initialize() {
        bienvenueLabel.setText("Bienvenue, Utilisateur");

        accueilButton.setOnAction(event -> showAccueil());
        evenementButton.setOnAction(event -> showEvenements());
        reservationButton.setOnAction(event -> showReservations());
        profilButton.setOnAction(event -> showProfil());
    }

    private void showAccueil() {
        loadView("/org/example/gestionproduit/view/accueil.fxml");
    }

    private void showEvenements() {
        loadView("/org/example/gestionproduit/view/evenement.fxml");
    }

    private void showReservations() {
        loadView("/org/example/gestionproduit/view/reservation.fxml");
    }

    private void showProfil() {
        loadView("/org/example/gestionproduit/view/profil.fxml");
    }

    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            mainPane.setCenter(view); // Remplace le contenu central
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de la vue : " + fxmlPath);
        }
    }

    // Pour passer le nom d'utilisateur dynamiquement
    public void setNomUtilisateur(String nom) {
        bienvenueLabel.setText("Bienvenue, " + nom);
    }
}
