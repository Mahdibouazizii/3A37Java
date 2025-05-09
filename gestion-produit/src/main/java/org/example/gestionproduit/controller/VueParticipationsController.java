package org.example.gestionproduit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.chart.PieChart; // N'oublie pas d'importer

import javafx.stage.Stage;
import org.example.gestionproduit.service.ServiceEvent;
import org.example.gestionproduit.service.ServiceParticipation;
import org.example.gestionproduit.entity.Participation;
import org.example.gestionproduit.service.UserService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.example.gestionproduit.entity.User;
import org.example.gestionproduit.entity.event;
public class VueParticipationsController {

    @FXML
    private VBox participationContainer; // Conteneur pour afficher les participations

    private ServiceParticipation serviceParticipation; // Service de gestion des participations
    private final UserService userService = new UserService();
    private final ServiceEvent serviceEvent = new ServiceEvent();

    // Constructeur
    public VueParticipationsController() {
        serviceParticipation = new ServiceParticipation(); // Initialisation du service
    }

    // Méthode d'initialisation
    @FXML
    public void initialize() {
        loadParticipations();
        afficherStatistiques(); // Appel de ta méthode après chargement
        // Charger les participations au démarrage
    }

    // Méthode pour charger toutes les participations
    private void loadParticipations() {
        participationContainer.getChildren().clear(); // Vider le conteneur

        // Appel à la méthode getAll() du service
        List<Participation> participations = serviceParticipation.getAll();

        // Ajouter chaque participation au conteneur
        for (Participation participation : participations) {
            HBox participationBox = new HBox(20); // Créer un HBox avec un espacement de 20px
            participationBox.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10px; -fx-padding: 15px; -fx-effect: dropshadow(gaussian, #7f8c8d, 10, 0, 0, 5);");

            // Créer les labels pour afficher les informations de la participation
            User user = userService.getById(participation.getIduser_id());
            String userName = user != null ? user.getNom() + " " + user.getPrenom() : "Utilisateur inconnu";
            Label userIdLabel = new Label("Utilisateur : " + userName);
            userIdLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");


            event evt = serviceEvent.getById(participation.getIdevenement_id());

            String eventName;
            if (evt != null) {
                eventName = evt.getNom();
                System.out.println("✅ Événement trouvé : " + eventName);
            } else {
                eventName = "❌ Événement introuvable (ID: " + participation.getIdevenement_id() + ")";
                System.err.println("⚠️ Aucun événement trouvé pour l'ID : " + participation.getIdevenement_id());
            }

            Label eventIdLabel = new Label("Événement : " + eventName);
            eventIdLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            Label statutLabel = new Label("Statut: " + participation.getStatut());
            statutLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #16a085;");

            Label placeLabel = new Label("Place Réservée: " + participation.getNbrplace());
            placeLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #f39c12;");

            Label ageLabel = new Label("Âge: " + participation.getAge());
            ageLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

            // Ajouter les labels dans le HBox
            participationBox.getChildren().addAll(userIdLabel, eventIdLabel, statutLabel, placeLabel, ageLabel);

            // Ajouter chaque HBox dans le VBox
            participationContainer.getChildren().add(participationBox);
        }
    }

    // Méthode pour afficher la fenêtre de saisie de participation
    @FXML
    public void onNessaisirParticipationClicked(ActionEvent event) {
        try {
            // Charger la vue FXML pour la saisie des participations
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/NessaisirParticipation.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la vue chargée
            Scene scene = new Scene(root);

            // Obtenir le stage actuel et appliquer la nouvelle scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la vue de saisie des participations.");
        }
    }

    @FXML
    public void goToAfficherEventBack(ActionEvent event) throws IOException {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
        Parent root = FXMLLoader.load(getClass().getResource("/AfficherEventBack.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Afficher les événements");
        stage.show();
    }

    @FXML
    public void goToProduitCrud(ActionEvent event) throws IOException {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
        Parent root = FXMLLoader.load(getClass().getResource("/org/example/gestionproduit/ProduitCrud.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Produit CRUD");
        stage.show();
    }

    @FXML
    public void goToUserView(ActionEvent event) throws IOException {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        currentStage.close();
        Parent root = FXMLLoader.load(getClass().getResource("/org/example/gestionproduit/UserView.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("User View");
        stage.show();
    }
    @FXML
    private void onViewParticipationsClicked(ActionEvent event) {
        try {
            // Charger la nouvelle vue FXML (vue des participations)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vueParticipations.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle scène avec la vue chargée
            Scene scene = new Scene(root);

            // Obtenir le stage actuel et appliquer la nouvelle scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Gestion des erreurs en cas de problème avec le chargement de la vue
            System.out.println("Erreur lors du chargement de la vue des participations.");
        }
    }

    private void afficherStatistiques() {
        List<Participation> participations = serviceParticipation.getAll();

        int moins18 = 0;
        int entre18et30 = 0;
        int plus30 = 0;

        Map<Integer, Integer> evenementPlaces = new HashMap<>();

        for (Participation participation : participations) {
            int age = participation.getAge();
            if (age < 18) {
                moins18++;
            } else if (age <= 30) {
                entre18et30++;
            } else {
                plus30++;
            }

            int eventId = participation.getIdevenement_id();
            int nbPlaces = participation.getNbrplace();
            evenementPlaces.put(eventId, evenementPlaces.getOrDefault(eventId, 0) + nbPlaces);
        }

        // --- PieChart pour les âges ---
        PieChart agePieChart = new PieChart();
        agePieChart.getData().add(new PieChart.Data("<18 ans", moins18));
        agePieChart.getData().add(new PieChart.Data("18-30 ans", entre18et30));
        agePieChart.getData().add(new PieChart.Data(">30 ans", plus30));
        agePieChart.setTitle("Statistiques selon l'âge ");
        agePieChart.setLabelsVisible(true);
        agePieChart.setLegendVisible(true);

        // --- PieChart pour les nombres de places ---
        PieChart eventPieChart = new PieChart();
        for (Map.Entry<Integer, Integer> entry : evenementPlaces.entrySet()) {
            PieChart.Data data = new PieChart.Data(entry.getKey() + " : " + entry.getValue() + " places", entry.getValue());
            eventPieChart.getData().add(data);
        }
        eventPieChart.setTitle("Statistiques selon l'événement le plus Réservé.");
        eventPieChart.setLabelsVisible(true);
        eventPieChart.setLegendVisible(true); // Afficher la légende automatique pour les événements

        // --- Placer les PieCharts côte à côte avec un HBox ---
        HBox chartsBox = new HBox(20); // Espacement entre les deux cercles
        chartsBox.getChildren().addAll(agePieChart, eventPieChart);

        // --- Ajouter tout dans le VBox principal ---
        VBox statisticsBox = new VBox(20);
        statisticsBox.getChildren().addAll(chartsBox); // Ajouter seulement les graphiques sans légende

        participationContainer.getChildren().add(statisticsBox); // Ajouter au conteneur
    }







}
