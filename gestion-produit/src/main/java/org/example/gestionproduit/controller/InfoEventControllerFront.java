    package org.example.gestionproduit.controller;

    // Importation des classes nécessaires
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
    import javafx.stage.Stage;
    import org.example.gestionproduit.entity.event;
    import org.example.gestionproduit.service.TranslationServiceSimple;

    import java.io.ByteArrayInputStream;
    import java.io.ByteArrayOutputStream;
    import java.io.File;
    import java.io.IOException;
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

        // Objet événement sélectionné
        private event selectedEvent;

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





    }
