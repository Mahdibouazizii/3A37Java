package org.example.gestionproduit.controller;

import com.itextpdf.text.*;
import org.example.gestionproduit.entity.event;
import org.example.gestionproduit.service.ServiceEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import com.itextpdf.text.pdf.PdfPCell;
import javafx.scene.layout.AnchorPane;  // ou autre type de conteneur utilisé pour la vue
import javafx.scene.image.Image;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;

import com.itextpdf.text.pdf.PdfPTable;


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
    @FXML private Button returnButton;

    // Objet représentant l'événement sélectionné
    private event selectedEvent;

    private EventController parentController;

    // Format d'affichage de l'heure
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public void setParentController(EventController controller) {
        this.parentController = controller;
    }

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
        heurDebutLabel.setText("Heure Début : " + e.getHeur_debut().toString());
        heurFinLabel.setText("Heure Fin : " + e.getHeur_fin().toString());
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
            showAlert(Alert.AlertType.ERROR, "Erreur", "Aucun événement sélectionné ou ID invalide.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation de suppression");
        confirm.setHeaderText(null);
        confirm.setContentText("Voulez-vous vraiment supprimer cet événement ?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Supprimer l'événement via le service
                new ServiceEvent().supprimer(selectedEvent.getId());
                showAlert(Alert.AlertType.INFORMATION, "Succès", "L'événement a été supprimé avec succès.");

                Stage stage = (Stage) deleteButton.getScene().getWindow();

                // Charger la vue AfficherEventBack.fxml après suppression
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestionproduit/BackofficeDashboard.fxml"));
                Parent root = loader.load();

                // Créer une nouvelle fenêtre pour afficher la vue AfficherEventBack
                Stage newStage = new Stage();
                newStage.setTitle("Afficher les événements");
                newStage.setScene(new Scene(root));
                newStage.show();

                // Fermer la fenêtre actuelle
                stage.close();

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

    // Méthode pour exporter l'événement en PDF
    public void onExportClicked() {
        Document document = new Document();
        try {
            String desktopPath = System.getProperty("user.home") + "/Desktop/pdf";
            File directory = new File(desktopPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = desktopPath + "/event_details_" + selectedEvent.getId() + ".pdf";
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD, BaseColor.DARK_GRAY);
            Font subtitleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLACK);
            Font textFont = new Font(Font.FontFamily.HELVETICA, 14, Font.NORMAL, BaseColor.GRAY);
            Font placeFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.RED);

            if (selectedEvent.getImage() != null && !selectedEvent.getImage().isEmpty()) {
                try {
                    com.itextpdf.text.Image eventImage = com.itextpdf.text.Image.getInstance(selectedEvent.getImage());
                    eventImage.scaleToFit(200, 200);
                    eventImage.setAlignment(Element.ALIGN_CENTER);
                    document.add(eventImage);
                    document.add(new Paragraph("\n"));
                } catch (Exception e) {
                    System.out.println("Erreur lors du chargement de l'image: " + e.getMessage());
                }
            }

            Paragraph eventName = new Paragraph(nomLabel.getText(), titleFont);
            eventName.setAlignment(Element.ALIGN_CENTER);
            document.add(eventName);

            document.add(new Paragraph("\n"));

            Paragraph description = new Paragraph(descriptionLabel.getText(), textFont);
            description.setAlignment(Element.ALIGN_CENTER);
            description.setSpacingAfter(10);
            document.add(description);

            document.add(new Paragraph("\n"));

            Paragraph startTime = new Paragraph("Début: " + heurDebutLabel.getText(), subtitleFont);
            startTime.setAlignment(Element.ALIGN_CENTER);
            document.add(startTime);

            document.add(new Paragraph("\n"));

            Paragraph endTime = new Paragraph("Fin: " + heurFinLabel.getText(), subtitleFont);
            endTime.setAlignment(Element.ALIGN_CENTER);
            document.add(endTime);

            document.add(new Paragraph("\n"));

            Paragraph places = new Paragraph("Places disponibles: " + placeLabel.getText(), placeFont);
            places.setAlignment(Element.ALIGN_CENTER);
            document.add(places);

            document.close();
            writer.close();

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Le PDF a été exporté avec succès : " + fileName);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur est survenue lors de l'exportation en PDF.");
        }
    }
    @FXML
    private void onReturnClicked() {
        try {
            // Charger la vue AfficherEventBack.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestionproduit/BackofficeDashboard.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène
            Stage stage = new Stage();
            stage.setTitle("Liste des événements");
            stage.setScene(new Scene(root));
            stage.show();

            // Fermer la fenêtre actuelle
            ((Stage) returnButton.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner à la liste des événements : " + e.getMessage());
        }
    }




}