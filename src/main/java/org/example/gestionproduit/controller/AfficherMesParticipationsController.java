package org.example.gestionproduit.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.example.gestionproduit.entity.Participation;
import org.example.gestionproduit.entity.User;
import org.example.gestionproduit.service.ServiceParticipation;
import org.example.gestionproduit.session.UserSession;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

import java.util.List;

public class AfficherMesParticipationsController {

    @FXML
    private VBox participationContainer;

    @FXML
    public void initialize() {
        // Attempt to retrieve the logged-in user from the session
        User loggedInUser = UserSession.getInstance().getUser();
        if (loggedInUser != null) {
            // If a user is logged in, load their participations
            loadParticipations(loggedInUser.getId());
        } else {
            // If no user is logged in, display an error message
            showError("L'information de l'utilisateur n'est pas disponible. Veuillez vous reconnecter.");
        }
    }

    private void loadParticipations(int userId) {
        // Clear any existing content in the container
        participationContainer.getChildren().clear();
        // Create an instance of the service to fetch participations
        ServiceParticipation service = new ServiceParticipation();
        // Retrieve the list of participations for the given user ID
        List<Participation> participations = service.getByUserId(userId);

        // Check if the list of participations is null or empty
        if (participations == null || participations.isEmpty()) {
            Label noParticipationLabel = new Label("Vous n'avez aucune participation.");
            participationContainer.getChildren().add(noParticipationLabel);
            return;
        }

        // Iterate through the list of participations and create a UI card for each
        for (Participation p : participations) {
            VBox card = new VBox(5);
            card.setPadding(new Insets(10));
            card.setStyle(
                    "-fx-border-color: #ccc;" +
                            "-fx-border-radius: 5;" +
                            "-fx-background-color: #f9f9f9;" +
                            "-fx-background-radius: 5;"
            );

            // Adding the participation details
            Label ageLabel = new Label("Âge: " + p.getAge());
            Label placesLabel = new Label("Nombre de places: " + p.getNbrplace());
            Label statutLabel = new Label("Statut: " + p.getStatut());
            Label eventIdLabel = new Label("ID Événement: " + p.getIdevenement_id()); // Affichage de l'ID de l'événement

            // Bouton Supprimer
            Button supprimerBtn = new Button("🗑 Supprimer");
            supprimerBtn.setStyle(
                    "-fx-background-color: #e74c3c;" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 5;"
            );
            // Bouton Modifier
            Button modifierBtn = new Button("📝 Modifier");
            modifierBtn.setStyle(
                    "-fx-background-color: #3498db;" +
                            "-fx-text-fill: white;" +
                            "-fx-background-radius: 5;" +
                            "-fx-margin-right: 10px;"
            );

            // Set the action for the delete button
            supprimerBtn.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("Supprimer cette participation ?");
                alert.setContentText("Êtes-vous sûr de vouloir supprimer cette participation ?");

                ButtonType oui = new ButtonType("Oui");
                ButtonType non = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(oui, non);

                alert.showAndWait().ifPresent(response -> {
                    if (response == oui) {
                        service.supprimer(p.getId());
                        loadParticipations(userId); // Refresh the list after deletion
                    }
                });
            });
            // Action Modifier
            modifierBtn.setOnAction(e -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierParticipation.fxml"));
                    Parent root = loader.load();

                    // Passer la participation sélectionnée au contrôleur
                    ModifierParticipationController controller = loader.getController();
                    controller.setParticipationToUpdate(p);  // méthode à créer

                    // Ouvrir dans une nouvelle fenêtre
                    Stage stage = new Stage();
                    stage.setTitle("Modifier Participation");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    showError("Erreur lors de l'ouverture de la fenêtre de modification.");
                }
            });
            HBox actionRow = new HBox(10, modifierBtn, supprimerBtn); // espacement de 10px entre les boutons

            actionRow.setPadding(new Insets(5, 0, 0, 0));

            // Adding all labels to the card
            card.getChildren().addAll(ageLabel, placesLabel, statutLabel, eventIdLabel, actionRow);
            participationContainer.getChildren().add(card);
        }
    }


    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // This method is now redundant as the userId is fetched from the session in initialize()
    // You can remove it if it's not used elsewhere.
    public void setUserId(int i) {
        System.out.println("AfficherMesParticipationsController - setUserId(int i) called (likely redundant).");
    }
}