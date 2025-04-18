package org.example.gestionproduit.controller;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import org.example.gestionproduit.entity.Reclamation;
import org.example.gestionproduit.entity.event;
import org.example.gestionproduit.service.ReclamationService;
import org.example.gestionproduit.service.ServiceEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.gestionproduit.session.UserSession;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
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
    @FXML
    private Button produitsButton;

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
    @FXML
    public void goToAfficherEvent(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEvent.fxml"));
        Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.setTitle("Liste des Événements");
        stage.show();
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
    @FXML
    private void handleViewProduit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestionproduit/ProductCardsFrontOffice.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle depuis le bouton ou le menu qui a déclenché l'événement
            Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void goToAfficherEventBack(ActionEvent event) throws IOException {
        // Fermer la fenêtre actuelle
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();

        // Charger le fichier FXML de la vue des événements
        Parent root = FXMLLoader.load(getClass().getResource("/AfficherEventBack.fxml"));

        // Créer une nouvelle scène
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Afficher les événements");
        stage.show();
    }

    @FXML
    public void goToProduitCrud(ActionEvent event) throws IOException {
        // Fermer la fenêtre actuelle
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();

        // Charger le fichier FXML de la vue Produit CRUD
        Parent root = FXMLLoader.load(getClass().getResource("/org/example/gestionproduit/ProduitCrud.fxml"));

        // Créer une nouvelle scène
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Produit CRUD");
        stage.show();
    }

    @FXML
    public void goToUserView(ActionEvent event) throws IOException {
        // Fermer la fenêtre actuelle
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();

        // Charger le fichier FXML de la vue UserView
        Parent root = FXMLLoader.load(getClass().getResource("/org/example/gestionproduit/UserView.fxml"));

        // Créer une nouvelle scène
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("User View");

        // Afficher la nouvelle fenêtre
        stage.show();
    }

}
