package org.example.gestionproduit.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import org.example.gestionproduit.entity.Participation;
import org.example.gestionproduit.entity.User;
import org.example.gestionproduit.entity.event;
import org.example.gestionproduit.service.ServiceEvent;
import org.example.gestionproduit.service.ServiceParticipation;
import org.example.gestionproduit.session.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AjouterParticipationController {

    @FXML private TextField ageField;
    @FXML private TextField nbrPlaceField;
    @FXML private ComboBox<String> statutComboBox;
    @FXML private Button validerButton;
    @FXML private Button annulerButton;

    private event selectedEvent;
    private Integer currentUserId;

    @FXML
    public void initialize() {
        statutComboBox.getItems().addAll("premium", "en attente", "standard");
        statutComboBox.setValue("en attente");

        User loggedInUser = UserSession.getInstance().getUser();
        if (loggedInUser != null) {
            this.currentUserId = loggedInUser.getId();
        } else {
            System.err.println("User not found in the session when adding participation.");
            showAlert(Alert.AlertType.ERROR, "Erreur", "L'information de l'utilisateur n'est pas disponible. Veuillez vous reconnecter.");
            validerButton.setDisable(true);
        }
    }

    public void setEvent(event e) {
        this.selectedEvent = e;
    }

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }

    @FXML
    private void onValiderClicked() {
        StringBuilder message = new StringBuilder("Veuillez corriger les champs suivants :\n");
        boolean hasError = false;
        String ageText = ageField.getText().trim();
        String nbrPlaceText = nbrPlaceField.getText().trim();
        String statut = statutComboBox.getValue();

        // Vérification des champs requis
        if (ageText.isEmpty()) {
            message.append("- Âge est requis\n");
            hasError = true;
        }
        if (nbrPlaceText.isEmpty()) {
            message.append("- Nombre de places est requis\n");
            hasError = true;
        }
        if (statut == null) {
            message.append("- Statut doit être sélectionné\n");
            hasError = true;
        }

        if (hasError) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", message.toString());
            return;
        }

        if (currentUserId == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "L'identifiant de l'utilisateur n'est pas disponible. Veuillez vous connecter.");
            return;
        }

        try {
            // Conversion des champs en entiers
            int age = Integer.parseInt(ageText);
            int nbrPlace = Integer.parseInt(nbrPlaceText);

            // Validation des valeurs saisies
            if (age < 14) {
                showAlert(Alert.AlertType.WARNING, "Âge invalide", "L'âge doit être supérieur à 14.");
                return;
            }

            if (nbrPlace < 1) {
                showAlert(Alert.AlertType.WARNING, "Nombre de places invalide", "Le nombre de places doit être supérieur à 1.");
                return;
            }

            // Vérification de l'événement sélectionné
            if (selectedEvent == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun événement n'est associé à cette participation.");
                return;
            }

            // Vérification si l'utilisateur a déjà participé à cet événement
            ServiceParticipation serviceParticipation = new ServiceParticipation();
            boolean isAlreadyParticipating = serviceParticipation.isUserAlreadyParticipating(currentUserId, selectedEvent.getId());

            if (isAlreadyParticipating) {
                showAlert(Alert.AlertType.WARNING, "Participation déjà existante", "Vous avez déjà participé à cet événement.");
                return;
            }

            // Vérification de la disponibilité des places
            if (nbrPlace > selectedEvent.getNbrplacetottale()) {
                showAlert(Alert.AlertType.WARNING, "Places insuffisantes",
                        "Le nombre de places demandées (" + nbrPlace + ") dépasse le nombre de places disponibles (" + selectedEvent.getNbrplacetottale() + ").");
                return;
            }

            // Création de la participation
            Participation participation = new Participation();
            participation.setIdevenement_id(selectedEvent.getId());
            participation.setIduser_id(currentUserId);
            participation.setAge(age);
            participation.setNbrplace(nbrPlace);
            participation.setStatut(statut);

            // Ajout de la participation
            serviceParticipation.ajouter(participation);

            // Décrémentation des places disponibles de l'événement
            ServiceEvent serviceEvent = new ServiceEvent();
            serviceEvent.decrementerPlacesDisponibles(selectedEvent.getId(), nbrPlace);

            // Mise à jour locale des places disponibles
            selectedEvent.setNbrplacetottale(selectedEvent.getNbrplacetottale() - nbrPlace);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Participation ajoutée avec succès.");

            // Fermer la fenêtre actuelle (AjouterParticipation.fxml)
            Stage currentStage = (Stage) validerButton.getScene().getWindow();
            currentStage.close();

            // Charger la vue AfficherEvent.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEvent.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Liste des Événements");
            stage.show();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Veuillez entrer des nombres valides pour l'âge et le nombre de places.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
        }
    }




    @FXML
    private void onAnnulerClicked() {
        closeStage();
    }

    private void closeStage() {
        Stage stage = (Stage) validerButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
