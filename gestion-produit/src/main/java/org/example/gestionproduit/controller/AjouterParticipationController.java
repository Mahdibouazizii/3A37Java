package org.example.gestionproduit.controller;

import org.example.gestionproduit.entity.Participation;
import org.example.gestionproduit.entity.event;
import org.example.gestionproduit.service.ServiceParticipation;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AjouterParticipationController {

    // Déclaration des éléments de l'interface utilisateur (UI)
    @FXML private TextField ageField; // Champ pour l'âge de l'utilisateur
    @FXML private TextField nbrPlaceField; // Champ pour le nombre de places à réserver
    @FXML private ComboBox<String> statutComboBox; // Liste déroulante pour sélectionner le statut
    @FXML private Button validerButton; // Bouton pour valider la participation
    @FXML private Button annulerButton; // Bouton pour annuler l'action

    // Variables de gestion des données de l'événement sélectionné et de l'utilisateur courant
    private event selectedEvent; // Événement auquel la participation est liée
    private int currentUserId = 1; // Identifiant de l'utilisateur courant (à remplacer par l'utilisateur connecté)

    // Initialisation de l'interface utilisateur
    @FXML
    public void initialize() {
        // Initialisation des valeurs possibles dans le ComboBox pour le statut
        statutComboBox.getItems().addAll("premium", "en attente", "standard");
        statutComboBox.setValue("en attente"); // Par défaut, on choisit "en attente"
    }

    // Méthode pour lier l'événement sélectionné à ce contrôleur
    public void setEvent(event e) {
        this.selectedEvent = e;
    }

    // Méthode appelée lors du clic sur le bouton "Valider"
    @FXML
    private void onValiderClicked() {
        // Vérification que tous les champs sont remplis
        if (ageField.getText().isEmpty() || nbrPlaceField.getText().isEmpty() || statutComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs.");
            return; // On arrête l'exécution si des champs sont vides
        }

        try {
            // Récupération et conversion des valeurs des champs de saisie
            int age = Integer.parseInt(ageField.getText().trim()); // Récupère l'âge, on le convertit en entier
            int nbrPlace = Integer.parseInt(nbrPlaceField.getText().trim()); // Récupère le nombre de places, on le convertit en entier
            String statut = statutComboBox.getValue(); // Récupère le statut sélectionné

            // Vérification qu'un événement a été sélectionné
            if (selectedEvent == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun événement n'est associé à cette participation.");
                return; // On arrête l'exécution si aucun événement n'est sélectionné
            }

            // Vérification que l'âge et le nombre de places sont valides (positifs)
            if (age <= 0 || nbrPlace <= 0) {
                showAlert(Alert.AlertType.WARNING, "Valeurs incorrectes", "L'âge et le nombre de places doivent être positifs.");
                return; // On arrête l'exécution si les valeurs sont incorrectes
            }

            // Création de l'objet Participation avec les données saisies
            Participation participation = new Participation();
            participation.setIdevenement_id(selectedEvent.getId()); // Associe l'événement à la participation
            participation.setIduser_id(currentUserId); // Associe l'utilisateur à la participation
            participation.setAge(age); // Définit l'âge de l'utilisateur
            participation.setNbrplace(nbrPlace); // Définit le nombre de places réservées
            participation.setStatut(statut); // Définit le statut de la participation

            // Utilisation du service pour ajouter la participation à la base de données
            ServiceParticipation service = new ServiceParticipation();
            service.ajouter(participation); // On ajoute la participation

            // Affichage d'un message de succès
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Participation ajoutée avec succès.");

            // Fermeture de la fenêtre après l'ajout de la participation
            closeStage();

        } catch (NumberFormatException e) {
            // Gestion des erreurs de saisie pour les champs numériques
            showAlert(Alert.AlertType.ERROR, "Erreur de saisie", "Veuillez entrer des nombres valides pour l'âge et le nombre de places.");
        } catch (Exception e) {
            // Gestion des autres erreurs potentielles
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue : " + e.getMessage());
        }
    }

    // Méthode appelée lors du clic sur le bouton "Annuler"
    @FXML
    private void onAnnulerClicked() {
        // Ferme la fenêtre sans effectuer d'action
        closeStage();
    }

    // Méthode pour fermer la fenêtre actuelle
    private void closeStage() {
        Stage stage = (Stage) validerButton.getScene().getWindow(); // Récupère la fenêtre actuelle
        stage.close(); // Ferme la fenêtre
    }

    // Méthode pour afficher une alerte (message d'information, d'erreur, etc.)
    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type); // Crée une alerte de type spécifié
        alert.setTitle(titre); // Définit le titre de l'alerte
        alert.setHeaderText(null); // Pas de texte d'en-tête
        alert.setContentText(message); // Définit le message à afficher dans l'alerte
        alert.showAndWait(); // Affiche l'alerte et attend que l'utilisateur la ferme
    }
}
