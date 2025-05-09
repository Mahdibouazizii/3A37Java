package org.example.gestionproduit.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class BackofficeDashboardController {

    @FXML
    private StackPane mainContent;

    @FXML
    public void initialize() {
        loadView("/org/example/gestionproduit/WelcomeAdmin.fxml");
    }



    @FXML
    public void loadUserView() {
        loadView("/org/example/gestionproduit/UserView.fxml");
    }

    @FXML
    public void loadProductView() {
        loadView("/org/example/gestionproduit/ProduitCrud.fxml"); // Or your actual file name
    }

    @FXML
    public void loadCommandeView() {
        loadView("/org/example/gestionproduit/CommandeList.fxml");
    }

    @FXML
    public void loadReclamationList() {
        loadView("/org/example/gestionproduit/ReclamationList.fxml");
    }
    @FXML
    public void loadEvent() {
        loadView("/AfficherEventBack.fxml");
    }
    @FXML
    public void loadParticipation() {
        loadView("/VueParticipations.fxml");
    }
    @FXML
    private void handleGoToFrontOffice() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestionproduit/ProductCardsFrontOffice.fxml"));
            Scene frontScene = new Scene(loader.load());

            Stage stage = (Stage) mainContent.getScene().getWindow(); // reuse same stage
            stage.setScene(frontScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }




    private void loadView(String fxmlPath) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlPath));
            mainContent.getChildren().setAll(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
