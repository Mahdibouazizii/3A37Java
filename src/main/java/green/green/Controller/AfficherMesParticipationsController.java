package green.green.Controller;

import green.green.Entity.Participation;
import green.green.services.ServiceParticipation;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.List;

public class AfficherMesParticipationsController {

    // Ce VBox est lié à l'élément VBox dans le FXML via fx:id="participationContainer"
    @FXML
    private VBox participationContainer;

    // ID de l'utilisateur courant
    private int userId;

    // Méthode appelée pour définir l'utilisateur courant et charger ses participations
    public void setUserId(int id) {
        this.userId = id;
        loadParticipations(); // Charger les participations après avoir défini l'ID
    }

    // Méthode pour charger les participations depuis la base de données et les afficher dans le VBox
    private void loadParticipations() {
        // On vide d'abord le conteneur pour éviter les doublons à chaque rechargement
        participationContainer.getChildren().clear();

        // Création d'une instance du service pour accéder aux participations
        ServiceParticipation service = new ServiceParticipation();

        // Récupération de la liste des participations de l'utilisateur
        List<Participation> participations = service.getByUserId(userId);

        // Pour chaque participation, on crée dynamiquement une "carte"
        for (Participation p : participations) {
            // VBox représentant une carte de participation
            VBox card = new VBox(5); // espace vertical de 5px entre les éléments
            card.setPadding(new Insets(10)); // marges internes
            card.setStyle(
                    "-fx-border-color: #ccc; " +                  // bord gris clair
                            "-fx-border-radius: 5; " +                    // coins arrondis pour la bordure
                            "-fx-background-color: #f9f9f9; " +           // fond gris très clair
                            "-fx-background-radius: 5;"                   // coins arrondis pour le fond
            );

            // Création des étiquettes pour les informations de participation
            Label ageLabel = new Label("Âge: " + p.getAge());
            Label placesLabel = new Label("Nombre de places: " + p.getNbrplace());
            Label statutLabel = new Label("Statut: " + p.getStatut());

            // Bouton pour supprimer la participation
            Button supprimerBtn = new Button("🗑 Supprimer");
            supprimerBtn.setStyle(
                    "-fx-background-color: #e74c3c; " +           // fond rouge
                            "-fx-text-fill: white; " +                    // texte en blanc
                            "-fx-background-radius: 5;"                   // coins arrondis
            );

            // Action associée au clic sur le bouton de suppression
            supprimerBtn.setOnAction(e -> {
                // Boîte de dialogue de confirmation
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText("Supprimer cette participation ?");
                alert.setContentText("Êtes-vous sûr de vouloir supprimer cette participation ?");

                // Boutons personnalisés
                ButtonType oui = new ButtonType("Oui");
                ButtonType non = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(oui, non);

                // Traitement du choix de l'utilisateur
                alert.showAndWait().ifPresent(response -> {
                    if (response == oui) {
                        service.supprimer(p.getId()); // Appel au service pour supprimer
                        loadParticipations(); // Recharger la liste après suppression
                    }
                });
            });

            // Ligne pour les boutons d'action (ici seulement le bouton Supprimer)
            HBox actionRow = new HBox(supprimerBtn);
            actionRow.setPadding(new Insets(5, 0, 0, 0)); // un peu de marge en haut

            // Ajouter les éléments dans la carte
            card.getChildren().addAll(ageLabel, placesLabel, statutLabel, actionRow);

            // Ajouter la carte dans le conteneur principal
            participationContainer.getChildren().add(card);
        }
    }
}
