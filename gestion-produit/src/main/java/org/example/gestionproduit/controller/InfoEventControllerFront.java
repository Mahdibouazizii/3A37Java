package org.example.gestionproduit.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import org.example.gestionproduit.entity.event;
import org.example.gestionproduit.service.TranslationServiceSimple;
import org.example.gestionproduit.session.UserSession;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import org.example.gestionproduit.entity.Reclamation;
import org.example.gestionproduit.service.ReclamationService;

import javafx.scene.layout.VBox;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import javafx.scene.control.TextField;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class InfoEventControllerFront {

    // Références aux composants FXML (définis dans le fichier FXML)
    @FXML private ImageView imageView;
    @FXML private Label nomLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label heurDebutLabel;
    @FXML private Label heurFinLabel;
    @FXML private Label placeLabel;
    @FXML private ImageView qrCodeImageView; // ImageView pour afficher le QR Code
    @FXML private FlowPane flowPane;
    // Objet événement sélectionné
    private event selectedEvent;
    @FXML private Button btnViewEvent;

    /**
     * Cette méthode est appelée pour initialiser l'affichage des détails de l'événement.
     * Elle met à jour les labels et l'image avec les informations de l'événement reçu.
     */
    public void setEvent(event e) {
        this.selectedEvent = e;

        // Remplir les labels avec les informations de l'événement
        nomLabel.setText("Nom : " + e.getNom());
        descriptionLabel.setText("Description : " + e.getDescription());
        heurDebutLabel.setText("Heure Début : " + e.getHeur_debut().toString());
        heurFinLabel.setText("Heure Fin : " + e.getHeur_fin().toString());
        placeLabel.setText("Places : " + e.getNbrplacetottale());

        // Chargement de l'image de l'événement
        try {
            File imageFile = new File(e.getImage());
            if (imageFile.exists() && imageFile.isFile()) {
                // Si le fichier existe et est valide, on le charge dans l'ImageView
                imageView.setImage(new Image(imageFile.toURI().toString()));
            } else {
                // Sinon, on affiche une image par défaut
                imageView.setImage(new Image(getClass().getResource("/default.jpg").toExternalForm()));
            }
        } catch (Exception ex) {
            // En cas d'erreur, on laisse l'imageView vide ou une image par défaut
            imageView.setImage(new Image(getClass().getResource("/default.jpg").toExternalForm()));
            System.out.println("Erreur lors du chargement de l'image de l'événement : " + ex.getMessage());
        }

        // Générer le QR code avec les informations de l'événement
        afficherQRCode();
    }

    /**
     * Générer un QR Code pour l'événement sélectionné.
     */
    public Image genererQRCode(String text, int width, int height) throws WriterException, IOException {
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        // Générer le QR Code à partir du texte fourni
        BitMatrix bitMatrix = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return new Image(new ByteArrayInputStream(pngOutputStream.toByteArray()));
    }

    /**
     * Afficher le QR Code avec les informations de l'événement.
     */
    public void afficherQRCode() {
        try {
            // Créer une chaîne avec les informations de l'événement
            String details = String.format("Nom : %s\nDescription : %s\nHeure Début : %s\nHeure Fin : %s\nPlaces : %d",
                    selectedEvent.getNom(),
                    selectedEvent.getDescription(),
                    selectedEvent.getHeur_debut(),
                    selectedEvent.getHeur_fin(),
                    selectedEvent.getNbrplacetottale());

            // Générer le QR Code avec les informations formatées
            Image qrCodeImage = genererQRCode(details, 200, 200);
            qrCodeImageView.setImage(qrCodeImage);
        } catch (WriterException | IOException e) {
            afficherAlerte("Erreur QR Code", "Échec de la génération du QR Code.", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /**
     * Méthode pour afficher une alerte avec un message spécifique.
     */
    private void afficherAlerte(String title, String headerText, String contentText, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    /**
     * Méthode appelée lorsqu'on clique sur le bouton "Ajouter Participation".
     * Elle ouvre une nouvelle fenêtre permettant d'ajouter une participation à l'événement sélectionné.
     */
    @FXML
    private void onAddParticipationClicked() {
        try {
            // Chargement du fichier FXML de l'ajout de participation
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterParticipation.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur associé au FXML
            AjouterParticipationController controller = loader.getController();

            // Passer l'événement sélectionné au contrôleur de participation
            controller.setEvent(selectedEvent);

            // Création et affichage de la nouvelle fenêtre
            Stage stage = new Stage();
            stage.setTitle("Ajouter Participation");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            // En cas d'erreur de chargement, afficher le message d'erreur dans la console
            System.out.println("❌ Erreur lors de l'ouverture de AjouterParticipation.fxml : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTranslateDescription() {
        if (selectedEvent == null) {
            afficherAlerte("Erreur", "Aucun événement sélectionné pour la traduction.", "", Alert.AlertType.WARNING);
            return;
        }

        try {
            String originalNom = selectedEvent.getNom();
            String originalDescription = selectedEvent.getDescription();

            if (originalDescription == null || originalDescription.trim().isEmpty()) {
                afficherAlerte("Erreur", "Aucune description disponible pour cet événement.", "", Alert.AlertType.WARNING);
                return;
            }

            // Traduction de la description uniquement
            if (descriptionLabel.getText().startsWith("Description (FR):")) {
                descriptionLabel.setText("Description : " + originalDescription);
            } else {
                String translatedDescription = TranslationServiceSimple.traduire(originalDescription, "fr");
                descriptionLabel.setText("Description (FR): " + translatedDescription);
            }

        } catch (Exception e) {
            e.printStackTrace();
            afficherAlerte("Erreur de traduction", "Impossible de traduire les détails de l'événement.", e.getMessage(), Alert.AlertType.ERROR);
        }
    }






    //////////////////////////////////:
    @FXML
    public void handleEvent(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEvent.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load the Event view.");
        }
    }

    @FXML
    public void handleMesCommandes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestionproduit/MesCommandes.fxml"));
            ScrollPane root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Mes Commandes");
            stage.setScene(new Scene(root, 700, 500));
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la vue des commandes.");
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        UserSession.getInstance().clearSession();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestionproduit/Login.fxml"));
            Parent loginRoot = loader.load();

            Stage stage = (Stage) btnViewEvent.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.setFullScreen(false);
            stage.setFullScreenExitHint("");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Échec du chargement de la page de connexion.");
        }
    }

    @FXML
    private void handleGoToAdmin() {
        if (UserSession.getInstance().getUser().getRoles().equalsIgnoreCase("admin")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestionproduit/BackofficeDashboard.fxml"));
                Scene dashboardScene = new Scene(loader.load());

                Stage stage = (Stage) flowPane.getScene().getWindow();
                stage.setScene(dashboardScene);
                stage.setFullScreen(true);
                stage.setFullScreenExitHint("");
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Access Denied", "Only admins can access the dashboard.");
        }
    }

    @FXML
    public void handleProduit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestionproduit/ProductCardsFrontOffice.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            stage.setTitle("Catalogue des Produits");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la vue des produits.");
        }
    }
    @FXML
    public void handleAddReclamation() {
        UserSession session = UserSession.getInstance();
        if (session == null || session.getUser() == null) {
            showAlert("Error", "You must be logged in to submit a reclamation.");
            return;
        }

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("New Reclamation");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setVgap(10);
        grid.setHgap(10);

        Label sujetLabel = new Label("Subject:");
        TextField sujetField = new TextField();
        Label descLabel = new Label("Description:");
        TextField descField = new TextField();

        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            String sujet = sujetField.getText().trim();
            String desc = descField.getText().trim();

            if (sujet.isEmpty() || desc.isEmpty()) {
                showAlert("Input Error", "All fields are required.");
                return;
            }

            Reclamation rec = new Reclamation(
                    session.getUser().getId(),
                    sujet,
                    desc,
                    new Timestamp(System.currentTimeMillis())
            );

            if (new ReclamationService().addReclamation(rec)) {
                showAlert("Success", "Reclamation submitted.");
                dialog.close();
            } else {
                showAlert("Error", "Failed to submit reclamation.");
            }
        });

        grid.add(sujetLabel, 0, 0);
        grid.add(sujetField, 1, 0);
        grid.add(descLabel, 0, 1);
        grid.add(descField, 1, 1);
        grid.add(submit, 1, 2);

        Scene scene = new Scene(grid);
        dialog.setScene(scene);
        dialog.showAndWait();
    }



    @FXML
    public void handleViewReclamations() {
        UserSession session = UserSession.getInstance();
        if (session == null || session.getUser() == null) {
            showAlert("Error", "You must be logged in to view reclamations.");
            return;
        }

        List<Reclamation> list = new ReclamationService().getReclamationsByUser(session.getUser().getId());
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        for (Reclamation r : list) {
            Label label = new Label(
                    r.getSujet() + "\n" + r.getDescription() + "\nDate: " + r.getCreatedAt() + "\n"
            );
            label.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 10;");
            box.getChildren().add(label);
        }

        Stage stage = new Stage();
        stage.setTitle("My Reclamations");
        stage.setScene(new Scene(new ScrollPane(box), 400, 300));
        stage.show();
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }



}
