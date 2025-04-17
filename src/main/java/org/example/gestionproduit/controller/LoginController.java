package org.example.gestionproduit.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.gestionproduit.entity.User;
import org.example.gestionproduit.service.UserService;
import org.example.gestionproduit.session.UserSession;

public class LoginController {

    @FXML
    private Hyperlink registerLink;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    private final UserService userService = new UserService();

    // Login logic goes here
    @FXML
    public void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        // Basic validation
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Email and password cannot be empty.");
            return;
        }

        // Validate user credentials and get the User object
        User user = userService.validateLogin(email, password);
        if (user != null) {
            // If login is successful, create the session and navigate to the product page
            UserSession.createSession(user);  // Initialize the user session
            showAlert("Success", "Login successful.");
            navigateToProductPage();
        } else {
            showAlert("Error", "Invalid email or password.");
        }
    }

    // This method will load and switch to ProductCardsFrontOffice.fxml
    private void navigateToProductPage() {
        try {
            // Load the ProductCardsFrontOffice.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestionproduit/ProductCardsFrontOffice.fxml"));
            BorderPane productPage = loader.load();  // Ensure the FXML is properly loaded

            // Optionally, pass the controller if necessary
            // ProductCardsFrontOfficeController controller = loader.getController();
            // controller.initializePage();

            // Create a new scene with the ProductCardsFrontOffice page
            Stage stage = (Stage) registerLink.getScene().getWindow();
            Scene productScene = new Scene(productPage);

            // Set the scene to the current stage
            stage.setScene(productScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the Product Cards page.");
        }
    }

    // Utility method to show an alert
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // This method is triggered when the "Register here" link is clicked
    @FXML
    public void handleRegisterLink() {
        try {
            // Load the Register screen FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestionproduit/Register.fxml"));
            AnchorPane registerPage = loader.load();

            // Create a new scene with the Register page
            Stage stage = (Stage) registerLink.getScene().getWindow();
            Scene registerScene = new Scene(registerPage);

            // Set the scene to the current stage
            stage.setScene(registerScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the Register page.");
        }
    }
}
