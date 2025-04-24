package org.example.gestionproduit.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.gestionproduit.entity.User;
import org.example.gestionproduit.service.UserService;

import java.io.File;

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
    public void initialize() {
        // No captcha logic needed
    }

    @FXML
    public void handleRegister() {
        // Reset styles
        nomField.getStyleClass().remove("input-error");
        prenomField.getStyleClass().remove("input-error");
        emailField.getStyleClass().remove("input-error");
        passwordField.getStyleClass().remove("input-error");
        adresseField.getStyleClass().remove("input-error");
        profilePictureField.getStyleClass().remove("input-error");

        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String adresse = adresseField.getText().trim();
        String profilePicture = profilePictureField.getText().trim();

        boolean hasError = false;
        StringBuilder errorMessages = new StringBuilder();

        if (nom.isEmpty()) {
            nomField.getStyleClass().add("input-error");
            errorMessages.append("- Le champ 'Nom' est requis.\n");
            hasError = true;
        }

        if (prenom.isEmpty()) {
            prenomField.getStyleClass().add("input-error");
            errorMessages.append("- Le champ 'Prénom' est requis.\n");
            hasError = true;
        }

        if (email.isEmpty() || !email.matches("^(.+)@(.+)$")) {
            emailField.getStyleClass().add("input-error");
            errorMessages.append("- L'adresse email est invalide.\n");
            hasError = true;
        }

        if (password.isEmpty() || password.length() < 6) {
            passwordField.getStyleClass().add("input-error");
            errorMessages.append("- Le mot de passe doit contenir au moins 6 caractères.\n");
            hasError = true;
        }

        if (adresse.isEmpty() || !adresse.matches("^[\\p{L}0-9 .,'-]{4,}$")) {
            adresseField.getStyleClass().add("input-error");
            errorMessages.append("- L'adresse est invalide ou trop courte.\n");
            hasError = true;
        }

        if (!profilePicture.isEmpty()) {
            File file = new File(profilePicture);
            if (!file.exists() || file.isDirectory()) {
                profilePictureField.getStyleClass().add("input-error");
                errorMessages.append("- L'image de profil sélectionnée n'existe pas.\n");
                hasError = true;
            }
        }

        if (hasError) {
            showAlert("Erreurs de saisie", errorMessages.toString());
            return;
        }

        // ✅ If valid
        User user = new User(0, nom, prenom, email, password, adresse, profilePicture, "user", false);
        boolean success = userService.registerUser(user);

        if (success) {
            showAlert("Succès", "Utilisateur enregistré avec succès.");
            handleLoginLink();
        } else {
            showAlert("Erreur", "Échec de l'inscription. Veuillez réessayer.");
        }
    }


    @FXML
    private void handleBrowsePicture() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Picture");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(registerButton.getScene().getWindow());
        if (selectedFile != null) {
            profilePictureField.setText(selectedFile.getAbsolutePath());
        }
    }

    @FXML
    public void handleLoginLink() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestionproduit/Login.fxml"));
            AnchorPane loginPage = loader.load();
            Stage stage = (Stage) registerButton.getScene().getWindow();
            Scene loginScene = new Scene(loginPage);
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
