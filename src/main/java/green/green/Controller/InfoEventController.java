package green.green.Controller;

import green.green.Entity.event;
import green.green.services.ServiceEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Optional;

public class InfoEventController {

    // Liens FXML vers les éléments de l'interface
    @FXML private ImageView imageView;
    @FXML private Label nomLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label heurDebutLabel;
    @FXML private Label heurFinLabel;
    @FXML private Label placeLabel;
    @FXML private Button deleteButton;
    @FXML private Button updateButton;

    // Objet représentant l'événement sélectionné
    private event selectedEvent;

    // Format d'affichage de l'heure
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    // Méthode appelée pour initialiser l'affichage de l'événement
    public void setEvent(event e) {
        this.selectedEvent = e;

        if (e == null) {
            // Affiche une alerte si aucun événement n'est passé
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun événement fourni.");
            return;
        }

        // Affiche les données de l'événement dans les labels
        nomLabel.setText("Nom : " + e.getNom());
        descriptionLabel.setText("Description : " + e.getDescription());
        heurDebutLabel.setText("Heure Début : " + timeFormat.format(e.getHeur_debut()));
        heurFinLabel.setText("Heure Fin : " + timeFormat.format(e.getHeur_fin()));
        placeLabel.setText("Places : " + e.getNbrplacetottale());

        // Charge et affiche l'image de l'événement
        loadImage(e.getImage());
    }

    // Méthode pour charger une image depuis un chemin donné
    private void loadImage(String imagePath) {
        try {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                // Si le fichier existe, le charger dans l'imageView
                imageView.setImage(new Image(imageFile.toURI().toString()));
            } else {
                // Sinon, charger une image par défaut
                imageView.setImage(new Image(getClass().getResource("/default.jpg").toExternalForm()));
            }
        } catch (Exception e) {
            // En cas d'erreur de chargement, vider l'imageView
            imageView.setImage(null);
            System.err.println("Erreur de chargement de l'image : " + e.getMessage());
        }
    }

    // Méthode appelée lorsque l'utilisateur clique sur "Supprimer"
    @FXML
    private void onDeleteClicked() {
        if (selectedEvent == null || selectedEvent.getId() <= 0) {
            // Vérifie si un événement est sélectionné
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun événement sélectionné ou ID invalide.");
            return;
        }

        // Boîte de dialogue de confirmation
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText(null);
        confirm.setContentText("Voulez-vous vraiment supprimer cet événement ?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Supprime l'événement via le service
                new ServiceEvent().supprimer(selectedEvent.getId());
                showAlert(Alert.AlertType.INFORMATION, "Succès", "L'événement a été supprimé avec succès.");

                // Ferme la fenêtre actuelle
                ((Stage) deleteButton.getScene().getWindow()).close();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Échec de suppression : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // Méthode appelée lorsque l'utilisateur clique sur "Modifier"
    @FXML
    private void onUpdateClicked() {
        if (selectedEvent == null || selectedEvent.getId() <= 0) {
            // Vérifie si un événement est sélectionné
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun événement sélectionné ou ID invalide.");
            return;
        }

        try {
            // Charge la vue FXML pour la modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateEvent.fxml"));
            Parent root = loader.load();

            // Récupère le contrôleur associé et lui passe l'événement à modifier
            UpdateEventController controller = loader.getController();
            controller.setEventToUpdate(selectedEvent);

            // Ouvre une nouvelle fenêtre pour la modification
            Stage stage = new Stage();
            stage.setTitle("Modifier l'Événement");
            stage.setScene(new Scene(root));
            stage.show();

            // Ferme la fenêtre actuelle
            ((Stage) updateButton.getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir la fenêtre de modification : " + e.getMessage());
        }
    }

    // Méthode utilitaire pour afficher une alerte
    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
