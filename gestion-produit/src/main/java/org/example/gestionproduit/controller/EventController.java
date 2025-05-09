package org.example.gestionproduit.controller;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
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
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.TextField;
import org.example.gestionproduit.util.BadWordsFilter;

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
    @FXML private Button produitsButton;
    @FXML private TextField searchField;
    @FXML private FlowPane flowPane;
    // Champs pour AfficherEvent.fxml / AfficherEventBack.fxml
    @FXML private VBox eventContainer;        // Conteneur des événements à afficher (ex: en cartes)
    @FXML private Button refreshButton;       // Bouton pour rafraîchir la liste des événements
    @FXML private Button addButton;           // Bouton pour ajouter un événement (admin seulement)
    @FXML private Button btnViewEvent;
    private String selectedImagePath = null;   // Stocke le chemin de l'image sélectionnée
    private boolean isAdmin = false;           // Flag pour déterminer si l'utilisateur est admin
    private EventController parentController;

    // Setter pour définir si on est en mode admin
    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin; // Détermine le mode d'affichage (admin ou utilisateur)
    }

    @FXML
    public void initialize() {
        // Méthode appelée lors de l'initialisation du contrôleur
        System.out.println("✅ EventController initialisé.");
        System.out.println("eventContainer: " + eventContainer); // Vérifier si c'est null

        searchField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                onSearchChanged(newValue);
            }
        });

        // Méthode appelée lorsqu'il y a un changement dans le champ de recherche

        // Détermine si on est en mode admin ou utilisateur
        if (addButton == null) {
            isAdmin = false; // Si le bouton d'ajout n'existe pas, on est en mode utilisateur
        } else {
            isAdmin = true; // Présence du bouton d'ajout → mode admin
        }

        loadEventsToListView(); // Charge la liste des événements
    }

    private void onSearchChanged(String query) {
        filterEvents(query); // Trigger the filtering logic
    }


    private void filterEvents(String query) {
        eventContainer.getChildren().clear(); // Clear existing content

        // Retrieve the list of events from the database
        List<event> events = getEventsFromDatabase();

        // Apply filtering based on the query
        for (event e : events) {
            if (e.getNom() != null && e.getNom().toLowerCase().contains(query.toLowerCase())) {
                eventContainer.getChildren().add(createEventCard(e)); // Add matching event to the container
            }
        }
    }


    // Exemple de méthode pour récupérer les événements depuis la base de données ou un service
    private List<event> getEventsFromDatabase() {
        ServiceEvent service = new ServiceEvent();
        try {
            return service.afficher(); // This method should return a list of events from your database
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void onRefreshClicked() {
        // Méthode pour rafraîchir la liste des événements
        loadEventsToListView();
    }

    @FXML
    private void onAddEventClicked() {
        try {
            // Ne pas fermer la fenêtre actuelle
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterEvent.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un événement");
            stage.setScene(new Scene(root));
            stage.setFullScreen(false);
            stage.setFullScreenExitHint("");
            stage.show();

        } catch (IOException e) {
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
    public void setParentController(EventController controller) {
        this.parentController = controller;
    }
    @FXML
    private void onCreateButtonClicked() {
        String nom = nameField.getText();
        String description = descriptionField.getText();
        LocalDate dateDebut = startDatePicker.getValue();
        LocalDate dateFin = endDatePicker.getValue();
        String nbPlacesText = maxPlaceField.getText();
        StringBuilder erreurs = new StringBuilder();

        // Vérification des champs
        if (nom == null || nom.trim().isEmpty()) {
            erreurs.append("- Le champ Nom est requis.\n");
        }
        if (description == null || description.trim().isEmpty()) {
            erreurs.append("- Le champ Description est requis.\n");
        }

        // Vérification des bad words dans le nom et la description
        if (BadWordsFilter.containsBadWordsInEvent(nom, description)) {
            erreurs.append("- Le nom ou la description contient des mots inappropriés.\n");
        }

        if (dateDebut == null) {
            erreurs.append("- La Date de début est requise.\n");
        }
        if (dateFin == null) {
            erreurs.append("- La Date de fin est requise.\n");
        }

        // Vérification de la condition "Date Début < Date Fin"
        if (dateDebut != null && dateFin != null && dateDebut.isAfter(dateFin)) {
            erreurs.append("- La Date de début doit être avant la Date de fin.\n");
        }
        // Vérification que la date de début est dans le futur (plus grande qu'aujourd'hui)
        if (dateDebut != null && dateDebut.isBefore(LocalDate.now())) {
            erreurs.append("- La Date de début doit être dans le futur.\n");
        }

        int nbPlaces = -1;
        try {
            nbPlaces = Integer.parseInt(nbPlacesText);
            if (nbPlaces <= 0) {
                erreurs.append("- Le nombre de places doit être supérieur à 0.\n");
            }
        } catch (NumberFormatException e) {
            erreurs.append("- Le champ Nombre de places doit contenir un nombre valide.\n");
        }

        // Affichage des erreurs s'il y en a
        if (erreurs.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de création");
            alert.setHeaderText("Veuillez corriger les erreurs suivantes :");
            alert.setContentText(erreurs.toString());
            alert.showAndWait();
            return;
        }

        // Traitement si tout est correct
        Date sqlDateDebut = Date.valueOf(dateDebut);
        Date sqlDateFin = Date.valueOf(dateFin);
        String image = (selectedImagePath != null) ? selectedImagePath : "default.jpg";
        event ev = new event(nom, description, sqlDateDebut, sqlDateFin, image, nbPlaces);

        try {
            ServiceEvent service = new ServiceEvent();
            service.ajouter(ev);
            System.out.println("✅ Événement ajouté avec succès !");
            clearForm();

            // 🔁 Rafraîchir la page précédente si elle est définie
            if (parentController != null) {
                parentController.loadEventsToListView();
            }

            // ✅ Fermer la fenêtre actuelle
            Stage currentStage = (Stage) createButton.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur");
            errorAlert.setHeaderText("Échec de l'ajout de l'événement");
            errorAlert.setContentText(e.getMessage());
            errorAlert.showAndWait();
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
        VBox card = new VBox();
        card.setSpacing(6);
        card.setStyle("-fx-background-color: #ffffff; -fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 6; -fx-background-radius: 6;");
        card.setMaxWidth(580);

        ImageView imageView = new ImageView();
        imageView.setFitWidth(560);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);

        try {
            if (e.getImage() != null && !e.getImage().isEmpty()) {
                File imageFile = new File(e.getImage());
                if (imageFile.exists()) {
                    imageView.setImage(new Image(imageFile.toURI().toString()));
                } else {
                    imageView.setImage(new Image(getClass().getResource("/default.jpg").toExternalForm()));
                }
            } else {
                imageView.setImage(new Image(getClass().getResource("/default.jpg").toExternalForm()));
            }
        } catch (Exception ex) {
            imageView.setImage(null);
        }

        Label nomLabel = new Label();
        if (e.getNom() == null || e.getNom().isEmpty()) {
            nomLabel.setText("Erreur : le champ Nom n'est pas renseigné.");
            nomLabel.setStyle("-fx-text-fill: red;");
        } else {
            nomLabel.setText("Nom : " + e.getNom());
            nomLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16;");
        }

        Label descLabel = new Label();
        if (e.getDescription() == null || e.getDescription().isEmpty()) {
            descLabel.setText("Erreur : le champ Description n'est pas renseigné.");
            descLabel.setStyle("-fx-text-fill: red;");
        } else {
            descLabel.setText("Description : " + e.getDescription());
        }

        Label dateDebutLabel = new Label();
        if (e.getHeur_debut() == null) {
            dateDebutLabel.setText("Erreur : le champ Date Début n'est pas renseigné.");
            dateDebutLabel.setStyle("-fx-text-fill: red;");
        } else {
            dateDebutLabel.setText("Date Début : " + e.getHeur_debut().toString());
        }

        Label dateFinLabel = new Label();
        if (e.getHeur_fin() == null) {
            dateFinLabel.setText("Erreur : le champ Date Fin n'est pas renseigné.");
            dateFinLabel.setStyle("-fx-text-fill: red;");
        } else {
            dateFinLabel.setText("Date Fin : " + e.getHeur_fin().toString());
        }

        Label placeLabel = new Label();
        if (e.getNbrplacetottale() <= 0) {
            placeLabel.setText("Erreur : le nombre de places doit être supérieur à 0.");
            placeLabel.setStyle("-fx-text-fill: red;");
        } else {
            placeLabel.setText("Places disponibles : " + e.getNbrplacetottale());
        }

        card.getChildren().addAll(imageView, nomLabel, descLabel, dateDebutLabel, dateFinLabel, placeLabel);

        card.setOnMouseClicked(ev -> {
            try {
                Stage currentStage = (Stage) ((Node) ev.getSource()).getScene().getWindow();
                currentStage.close();

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
                stage.setFullScreen(true);               // ✅ Fullscreen mode
                stage.setFullScreenExitHint("");         // ✅ Optional: no exit message
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        return card;
    }

    @FXML
    private void onMesParticipationsClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherMesParticipations.fxml"));
            Parent root = loader.load();
            AfficherMesParticipationsController controller = loader.getController();
            controller.setUserId(1);

            Stage stage = new Stage();
            stage.setTitle("Mes Participations");
            stage.setScene(new Scene(root));
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onViewParticipationsClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vueParticipations.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
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
