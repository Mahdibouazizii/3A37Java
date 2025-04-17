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
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class EventController {
    // Champs pour AjouterEvent.fxml
    @FXML private DatePicker startDatePicker; // Sélecteur de date de début
    @FXML private DatePicker endDatePicker;   // Sélecteur de date de fin
    @FXML private TextField nameField;        // Champ de texte pour le nom de l'événement
    @FXML private TextField descriptionField; // Champ de texte pour la description de l'événement
    @FXML private TextField maxPlaceField;    // Champ de texte pour le nombre maximum de places
    @FXML private ImageView imageView;        // Affichage de l'image sélectionnée pour l'événement
    @FXML private Button createButton;        // Bouton pour créer un événement
    @FXML private Button chooseImageButton;   // Bouton pour choisir une image

    // Champs pour AfficherEvent.fxml / AfficherEventBack.fxml
    @FXML private VBox eventContainer;        // Conteneur des événements à afficher (ex: en cartes)
    @FXML private Button refreshButton;       // Bouton pour rafraîchir la liste des événements
    @FXML private Button addButton;           // Bouton pour ajouter un événement (admin seulement)

    private String selectedImagePath = null;   // Stocke le chemin de l'image sélectionnée
    private boolean isAdmin = false;           // Flag pour déterminer si l'utilisateur est admin

    // Setter pour définir si on est en mode admin
    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin; // Détermine le mode d'affichage (admin ou utilisateur)
    }

    @FXML
    public void initialize() {
        // Méthode appelée lors de l'initialisation du contrôleur
        System.out.println("✅ EventController initialisé.");

        // Détermine si on est en mode admin ou utilisateur
        if (addButton == null) {
            isAdmin = false; // Si le bouton d'ajout n'existe pas, on est en mode utilisateur
        } else {
            isAdmin = true; // Présence du bouton d'ajout → mode admin
        }

        loadEventsToListView(); // Charge la liste des événements
    }

    @FXML
    private void onRefreshClicked() {
        // Méthode pour rafraîchir la liste des événements
        loadEventsToListView();
    }

    @FXML
    private void onAddEventClicked() {
        // Méthode pour ouvrir la fenêtre d'ajout d'événement
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterEvent.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un événement");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("❌ Erreur lors de l'ouverture de AjouterEvent.fxml : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void onChooseImageClicked() {
        // Méthode pour choisir une image à partir d'un FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Fichiers image", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            selectedImagePath = selectedFile.getAbsolutePath(); // Stocke le chemin de l'image
            Image image = new Image(selectedFile.toURI().toString()); // Charge l'image sélectionnée
            imageView.setImage(image); // Affiche l'image dans le ImageView
            System.out.println("📷 Image sélectionnée : " + selectedImagePath);
        } else {
            System.out.println("⚠️ Aucune image sélectionnée.");
        }
    }

    @FXML
    private void onCreateButtonClicked() {
        // Méthode pour créer un événement
        String nom = nameField.getText();
        String description = descriptionField.getText();
        LocalDate dateDebut = startDatePicker.getValue();
        LocalDate dateFin = endDatePicker.getValue();
        int nbPlaces;
        try {
            nbPlaces = Integer.parseInt(maxPlaceField.getText()); // Conversion du nombre de places
        } catch (NumberFormatException e) {
            System.out.println("❌ Nombre de places invalide.");
            return;
        }

        // Vérification si tous les champs sont remplis
        if (nom.isEmpty() || description.isEmpty() || dateDebut == null || dateFin == null) {
            System.out.println("❌ Veuillez remplir tous les champs.");
            return;
        }

        // Conversion des dates pour la base de données
        Date sqlDateDebut = Date.valueOf(dateDebut);
        Date sqlDateFin = Date.valueOf(dateFin);
        String image = (selectedImagePath != null) ? selectedImagePath : "default.jpg"; // Image par défaut si aucune image sélectionnée

        // Création de l'objet événement
        event ev = new event(nom, description, sqlDateDebut, sqlDateFin, image, nbPlaces);

        try {
            // Appel du service pour ajouter l'événement
            ServiceEvent service = new ServiceEvent();
            service.ajouter(ev);
            System.out.println("✅ Événement ajouté avec succès !");
            clearForm(); // Réinitialisation du formulaire
            Stage stage = (Stage) createButton.getScene().getWindow();
            stage.close(); // Fermeture de la fenêtre
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de l'ajout de l'événement : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearForm() {
        // Réinitialise tous les champs du formulaire
        nameField.clear();
        descriptionField.clear();
        maxPlaceField.clear();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        imageView.setImage(null);
        selectedImagePath = null;
    }

    private void loadEventsToListView() {
        // Charge et affiche la liste des événements dans le conteneur
        if (eventContainer == null) {
            System.out.println("⚠️ eventContainer non initialisé (peut-être pas sur la bonne page).");
            return;
        }

        eventContainer.getChildren().clear(); // Vide le conteneur avant de le remplir

        try {
            ServiceEvent service = new ServiceEvent();
            List<event> events = service.afficher(); // Récupère la liste des événements

            // Crée une carte pour chaque événement et l'ajoute au conteneur
            for (event e : events) {
                VBox card = createEventCard(e);
                eventContainer.getChildren().add(card);
            }

            System.out.println("✅ Liste des événements chargée.");
        } catch (Exception e) {
            System.out.println("❌ Erreur lors du chargement des événements : " + e.getMessage());
        }
    }

    private VBox createEventCard(event e) {
        // Crée une carte pour afficher un événement
        VBox card = new VBox();
        card.setSpacing(6);
        card.setStyle("-fx-background-color: #ffffff; -fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 6; -fx-background-radius: 6;");
        card.setMaxWidth(580);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(560);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);

        // Charge l'image de l'événement, ou une image par défaut si elle n'existe pas
        try {
            File imageFile = new File(e.getImage());
            if (imageFile.exists()) {
                imageView.setImage(new Image(imageFile.toURI().toString()));
            } else {
                imageView.setImage(new Image(getClass().getResource("/default.jpg").toExternalForm()));
            }
        } catch (Exception ex) {
            imageView.setImage(null);
        }

        // Crée des labels pour les informations de l'événement
        Label nomLabel = new Label("Nom : " + e.getNom());
        nomLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        Label descLabel = new Label("Description : " + e.getDescription());
        Label dateDebutLabel = new Label("Date Début : " + e.getHeur_debut());
        Label dateFinLabel = new Label("Date Fin : " + e.getHeur_fin());
        Label placeLabel = new Label("Places disponibles : " + e.getNbrplacetottale());

        card.getChildren().addAll(imageView, nomLabel, descLabel, dateDebutLabel, dateFinLabel, placeLabel);

        // Clic sur la carte pour afficher les détails
        card.setOnMouseClicked(ev -> {
            try {
                String fxmlPath = isAdmin ? "/InfoEvent.fxml" : "/InfoEventFront.fxml";
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();

                if (isAdmin) {
                    InfoEventController controller = loader.getController();
                    controller.setEvent(e);
                } else {
                    InfoEventControllerFront controller = loader.getController();
                    controller.setEvent(e);
                }

                Stage stage = new Stage();
                stage.setTitle("Détails de l'événement");
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        return card;
    }

    @FXML
    private void onMesParticipationsClicked() {
        // Méthode pour afficher les participations de l'utilisateur
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherMesParticipations.fxml"));
            Parent root = loader.load();

            // Passer l'ID de l'utilisateur si besoin
            AfficherMesParticipationsController controller = loader.getController();
            controller.setUserId(1); // Remplace 1 par l'ID réel du participant connecté

            Stage stage = new Stage();
            stage.setTitle("Mes Participations");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("❌ Erreur lors de l'ouverture de Mes Participations : " + e.getMessage());
            e.printStackTrace();
        }
    }

}
