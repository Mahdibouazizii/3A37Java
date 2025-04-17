package green.green.Controller;

// Importation des classes nécessaires
import green.green.Entity.event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class InfoEventControllerFront {

    // Références aux composants FXML (définis dans le fichier FXML)
    @FXML private ImageView imageView;
    @FXML private Label nomLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label heurDebutLabel;
    @FXML private Label heurFinLabel;
    @FXML private Label placeLabel;

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
            if (imageFile.exists()) {
                // Si le fichier existe, on le charge dans l'ImageView
                imageView.setImage(new Image(imageFile.toURI().toString()));
            } else {
                // Sinon, on affiche une image par défaut
                imageView.setImage(new Image(getClass().getResource("/default.jpg").toExternalForm()));
            }
        } catch (Exception ex) {
            // En cas d'erreur, on laisse l'imageView vide
            imageView.setImage(null);
        }
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
}
