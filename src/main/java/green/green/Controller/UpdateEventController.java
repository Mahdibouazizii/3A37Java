package green.green.Controller;

// Importation des classes nécessaires
import green.green.Entity.event;
import green.green.services.ServiceEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class UpdateEventController {

    // Références aux éléments de l'interface graphique définis dans le fichier FXML
    @FXML private TextField nomTextField;
    @FXML private TextArea descriptionTextArea;
    @FXML private TextField placesField;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private ImageView eventImageView;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    @FXML private Button chooseImageButton;

    // L'événement à modifier
    private event eventToUpdate;

    // Fichier d'image sélectionné
    private File selectedImageFile;

    // Format pour l'heure (non utilisé ici, mais pourrait être utile si tu ajoutes les heures précisément)
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    /**
     * Méthode appelée pour initialiser le formulaire avec les informations de l'événement à modifier
     */
    public void setEventToUpdate(event e) {
        this.eventToUpdate = e;

        if (e != null) {
            // Pré-remplissage des champs avec les valeurs existantes de l'événement
            nomTextField.setText(e.getNom());
            descriptionTextArea.setText(e.getDescription());
            placesField.setText(String.valueOf(e.getNbrplacetottale()));

            // Conversion des dates de début et fin en LocalDate pour les DatePickers
            if (e.getHeur_debut() != null) {
                dateDebutPicker.setValue(e.getHeur_debut().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
            if (e.getHeur_fin() != null) {
                dateFinPicker.setValue(e.getHeur_fin().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }

            // Chargement de l'image si elle existe
            if (e.getImage() != null && !e.getImage().isEmpty()) {
                File imageFile = new File(e.getImage());
                if (imageFile.exists()) {
                    eventImageView.setImage(new Image(imageFile.toURI().toString()));
                }
            }
        }
    }

    /**
     * Méthode appelée lorsqu'on clique sur le bouton "Enregistrer"
     */
    @FXML
    private void onSaveClicked() {
        if (eventToUpdate == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun événement sélectionné.");
            return;
        }

        // Récupération et validation des données entrées par l'utilisateur
        String nom = nomTextField.getText().trim();
        String description = descriptionTextArea.getText().trim();
        String placesText = placesField.getText().trim();
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();

        if (nom.isEmpty() || description.isEmpty()
                || placesText.isEmpty() || dateDebut == null || dateFin == null) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir tous les champs.");
            return;
        }

        try {
            int places = Integer.parseInt(placesText); // Vérifie que c'est bien un nombre entier

            // Conversion des LocalDate en Date (pour être compatible avec l'entité)
            Date dateDebutConverted = Date.from(dateDebut.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date dateFinConverted = Date.from(dateFin.atStartOfDay(ZoneId.systemDefault()).toInstant());

            // Mise à jour de l'objet événement
            eventToUpdate.setNom(nom);
            eventToUpdate.setDescription(description);
            eventToUpdate.setNbrplacetottale(places);
            eventToUpdate.setHeur_debut(dateDebutConverted);
            eventToUpdate.setHeur_fin(dateFinConverted);

            // Si une nouvelle image a été choisie, on met à jour le chemin
            if (selectedImageFile != null) {
                eventToUpdate.setImage(selectedImageFile.getAbsolutePath()); // À adapter si tu veux gérer les chemins relatifs
            }

            // Appel au service pour enregistrer les modifications dans la base
            new ServiceEvent().modifier(eventToUpdate);

            // Confirmation pour l'utilisateur
            showAlert(Alert.AlertType.INFORMATION, "Succès", "L'événement a été mis à jour avec succès.");

            // Ferme la fenêtre après sauvegarde
            ((Stage) saveButton.getScene().getWindow()).close();

        } catch (NumberFormatException e) {
            // Gestion des erreurs si l'utilisateur n'entre pas un nombre valide
            showAlert(Alert.AlertType.ERROR, "Nombre invalide", "Veuillez entrer un nombre entier valide pour les places.");
        } catch (Exception e) {
            // Autres erreurs générales
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite : " + e.getMessage());
        }
    }

    /**
     * Méthode appelée lorsqu'on clique sur le bouton "Annuler"
     * Ferme simplement la fenêtre sans enregistrer
     */
    @FXML
    private void onCancelClicked() {
        ((Stage) cancelButton.getScene().getWindow()).close();
    }

    /**
     * Méthode appelée lorsqu'on clique sur "Choisir une image"
     * Ouvre un sélecteur de fichier pour choisir une nouvelle image
     */
    @FXML
    private void onChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            eventImageView.setImage(new Image(file.toURI().toString()));
        }
    }

    /**
     * Affiche une alerte personnalisée
     */
    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
