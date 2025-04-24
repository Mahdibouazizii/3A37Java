package org.example.gestionproduit.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.example.gestionproduit.entity.User;
import org.example.gestionproduit.service.UserService;
import org.example.gestionproduit.session.UserSession;

import java.util.Random;

public class LoginController {

    @FXML
    private Hyperlink registerLink;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label captchaQuestionLabel;

    @FXML
    private TextField captchaAnswerField;

    private int correctCaptchaAnswer;

    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        generateCaptchaQuestion();
    }

    private void generateCaptchaQuestion() {
        Random rand = new Random();
        int a = rand.nextInt(10) + 1;
        int b = rand.nextInt(10) + 1;
        correctCaptchaAnswer = a + b;
        captchaQuestionLabel.setText("What is " + a + " + " + b + "?");
        captchaAnswerField.clear();
    }

    @FXML
    public void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();
        String captchaAnswer = captchaAnswerField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Email and password cannot be empty.");
            return;
        }

        if (!captchaAnswer.matches("\\d+") || Integer.parseInt(captchaAnswer) != correctCaptchaAnswer) {
            showAlert("Error", "Incorrect CAPTCHA answer.");
            generateCaptchaQuestion(); // Refresh CAPTCHA
            return;
        }

        User user = userService.validateLogin(email, password);
        if (user != null) {
            if (user.isBanned()) {
                showAlert("Access Denied", "Your account has been banned. Please contact support.");
                return;
            }
            UserSession.createSession(user);
            showAlert("Success", "Login successful.");
            navigateToProductPage();
        } else {
            showAlert("Error", "Invalid email or password.");
        }
    }

    private void navigateToProductPage() {
        try {
            FXMLLoader loader;
            User user = UserSession.getInstance().getUser();

            if ("admin".equalsIgnoreCase(user.getRole())) {
                loader = new FXMLLoader(getClass().getResource("/org/example/gestionproduit/BackofficeDashboard.fxml"));
            } else {
                loader = new FXMLLoader(getClass().getResource("/org/example/gestionproduit/ProductCardsFrontOffice.fxml"));
            }

            Scene scene = new Scene(loader.load(), 1200, 700);
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the main interface.");
        }
    }

    @FXML
    public void handleForgotPassword() {
        TextInputDialog emailPrompt = new TextInputDialog();
        emailPrompt.setTitle("Mot de passe oublié");
        emailPrompt.setHeaderText("Entrez votre email");
        emailPrompt.setContentText("Email:");

        emailPrompt.showAndWait().ifPresent(email -> {
            User user = userService.findUserByEmail(email);
            if (user == null) {
                showAlert("Erreur", "Aucun utilisateur avec cet email.");
                return;
            }

            String generatedKey = generateResetKey();
            user.setResetKey(generatedKey);

            if (userService.sendResetEmail(user.getEmail(), generatedKey)) {
                TextInputDialog keyPrompt = new TextInputDialog();
                keyPrompt.setTitle("Clé de réinitialisation");
                keyPrompt.setHeaderText("Vérifiez votre boîte mail");
                keyPrompt.setContentText("Entrez la clé:");

                keyPrompt.showAndWait().ifPresent(enteredKey -> {
                    if (enteredKey.equals(generatedKey)) {
                        Dialog<Pair<String, String>> passwordDialog = new Dialog<>();
                        passwordDialog.setTitle("Réinitialiser le mot de passe");

                        GridPane grid = new GridPane();
                        grid.setHgap(10);
                        grid.setVgap(10);
                        grid.setPadding(new Insets(20, 150, 10, 10));

                        PasswordField newPass = new PasswordField();
                        newPass.setPromptText("Nouveau mot de passe");
                        PasswordField confirmPass = new PasswordField();
                        confirmPass.setPromptText("Confirmer le mot de passe");

                        grid.add(new Label("Nouveau:"), 0, 0);
                        grid.add(newPass, 1, 0);
                        grid.add(new Label("Confirmer:"), 0, 1);
                        grid.add(confirmPass, 1, 1);

                        passwordDialog.getDialogPane().setContent(grid);
                        passwordDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

                        passwordDialog.setResultConverter(dialogBtn -> {
                            if (dialogBtn == ButtonType.OK) {
                                return new Pair<>(newPass.getText(), confirmPass.getText());
                            }
                            return null;
                        });

                        passwordDialog.showAndWait().ifPresent(pair -> {
                            if (!pair.getKey().equals(pair.getValue())) {
                                showAlert("Erreur", "Les mots de passe ne correspondent pas.");
                            } else {
                                if (userService.updatePassword(user.getId(), pair.getKey())) {
                                    showAlert("Succès", "Mot de passe mis à jour.");
                                } else {
                                    showAlert("Erreur", "Échec de la mise à jour.");
                                }
                            }
                        });

                    } else {
                        showAlert("Erreur", "Clé incorrecte.");
                    }
                });

            } else {
                showAlert("Erreur", "Échec d'envoi de l'email.");
            }
        });
    }

    private String generateResetKey() {
        return java.util.UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    @FXML
    public void handleRegisterLink() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestionproduit/Register.fxml"));
            AnchorPane registerPage = loader.load();

            Stage stage = (Stage) registerLink.getScene().getWindow();
            Scene registerScene = new Scene(registerPage);

            stage.setScene(registerScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load the Register page.");
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
