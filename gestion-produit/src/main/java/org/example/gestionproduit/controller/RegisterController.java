package org.example.gestionproduit.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.gestionproduit.entity.User;
import org.example.gestionproduit.service.UserService;

public class RegisterController {

    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField adresseField;
    @FXML
    private TextField profilePictureField;
    @FXML
    private Button registerButton;

    private final UserService userService = new UserService();

    @FXML
    public void handleRegister() {
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String adresse = adresseField.getText().trim();
        String profilePicture = profilePictureField.getText().trim();

        // Basic input validation
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "All fields must be filled out.");
            return;
        }

        // Create a User object with the provided data
        User user = new User(0, nom, prenom, email, password, adresse, profilePicture, "user", false);

        // Register the user through UserService
        boolean success = userService.registerUser(user);
        if (success) {
            showAlert("Success", "User registered successfully.");
            // Optionally, navigate to the login screen
            handleLoginLink();
        } else {
            showAlert("Error", "Registration failed. Please try again.");
        }
    }

    @FXML
    public void handleLoginLink() {
        try {
            // Load the Login screen FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestionproduit/Login.fxml"));
            AnchorPane loginPage = loader.load();

            // Create a new scene with the Login page
            Stage stage = (Stage) registerButton.getScene().getWindow();
            Scene loginScene = new Scene(loginPage);

            // Set the scene to the current stage
            stage.setScene(loginScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the Login page.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
