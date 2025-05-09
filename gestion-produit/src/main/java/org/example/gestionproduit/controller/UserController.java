package org.example.gestionproduit.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import org.example.gestionproduit.entity.User;
import org.example.gestionproduit.service.UserService;

import java.io.File;

public class UserController {

    private UserService userService = new UserService();

    @FXML private ListView<User> userListView;
    @FXML private TextField firstNameField, lastNameField, emailField, passwordField, addressField,
            profilePictureField, roleField, isBannedField;
    @FXML private Button addUserButton, updateUserButton, deleteUserButton;
    @FXML private Text statusLabel;
    @FXML private VBox userCardContainer;

    private ObservableList<User> users;

    @FXML
    public void initialize() {
        refreshUserCards();
    }

    private void refreshUserCards() {
        userCardContainer.getChildren().clear();
        for (User user : userService.getAllUsers()) {
            userCardContainer.getChildren().add(createUserCard(user));
        }
    }

    private HBox createUserCard(User user) {
        HBox card = new HBox(10);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-border-color: #ccc; -fx-background-color: #f9f9f9; -fx-border-radius: 5; -fx-background-radius: 5;");
        card.setPrefWidth(700);

        ImageView profileImageView = new ImageView();
        profileImageView.setFitHeight(64);
        profileImageView.setFitWidth(64);
        profileImageView.setPreserveRatio(true);

        if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
            try {
                File imageFile = new File(user.getProfilePicture());
                if (imageFile.exists()) {
                    profileImageView.setImage(new Image(imageFile.toURI().toString()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        VBox userInfo = new VBox(5);
        userInfo.getChildren().addAll(
                new Label("👤 " + user.getNom() + " " + user.getPrenom()),
                new Label("📧 " + user.getEmail()),
                new Label("🏠 " + user.getAdresse()),
                new Label("🧑‍💼 Role: " + user.getRoles() + " | 🚫 Banned: " + user.isBanned())
        );

        Button editBtn = new Button("✏️ Edit");
        editBtn.setOnAction(e -> showEditUserDialog(user));

        Button deleteBtn = new Button("🗑 Delete");
        deleteBtn.setOnAction(e -> {
            userService.deleteUser(user);
            refreshUserCards();
        });

        Button toggleBanBtn = new Button(user.isBanned() ? "✅ Unban" : "🚫 Ban");
        toggleBanBtn.setOnAction(e -> {
            boolean newStatus = !user.isBanned();
            user.setBanned(newStatus);
            if (userService.updateUser(user)) {
                refreshUserCards();
            }
        });

        Button toggleRoleBtn = new Button("🔁 Toggle Role");
        toggleRoleBtn.setOnAction(e -> toggleUserRole(user));

        VBox actions = new VBox(5, editBtn, deleteBtn, toggleRoleBtn, toggleBanBtn);
        card.getChildren().addAll(profileImageView, userInfo, actions);
        return card;
    }

    @FXML
    public void addUser() {
        User newUser = new User(0, firstNameField.getText(), lastNameField.getText(), emailField.getText(),
                passwordField.getText(), addressField.getText(), profilePictureField.getText(),
                roleField.getText(), Boolean.parseBoolean(isBannedField.getText()));
        if (userService.addUser(newUser)) {
            statusLabel.setText("User added successfully.");
            users.add(newUser);
            clearFields();
        } else {
            statusLabel.setText("Failed to add user.");
        }
    }

    @FXML
    private void showAddUserDialog() {
        Dialog<User> dialog = getUserDialog("Add New User", null);
        dialog.showAndWait().ifPresent(newUser -> {
            userService.addUser(newUser);
            refreshUserCards();
        });
    }

    private void showEditUserDialog(User existingUser) {
        Dialog<User> dialog = getUserDialog("Edit User", existingUser);
        dialog.showAndWait().ifPresent(updatedUser -> {
            updatedUser.setId(existingUser.getId());
            if (userService.updateUser(updatedUser)) {
                refreshUserCards();
            }
        });
    }

    private Dialog<User> getUserDialog(String title, User existingUser) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle(title);

        Label fnLabel = new Label("First Name:");
        TextField fnField = new TextField(existingUser != null ? existingUser.getNom() : "");

        Label lnLabel = new Label("Last Name:");
        TextField lnField = new TextField(existingUser != null ? existingUser.getPrenom() : "");

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField(existingUser != null ? existingUser.getEmail() : "");

        Label addrLabel = new Label("Address:");
        TextField addrField = new TextField(existingUser != null ? existingUser.getAdresse() : "");

        Label picLabel = new Label("Profile Picture:");
        TextField picField = new TextField(existingUser != null ? existingUser.getProfilePicture() : "");
        picField.setEditable(false);

        Button browseBtn = new Button("Browse");
        browseBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Profile Picture");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                picField.setText(selectedFile.getAbsolutePath());
            }
        });

        Label roleLabel = new Label("Role:");
        TextField roleField = new TextField(existingUser != null ? existingUser.getRoles() : "");

        Label banLabel = new Label("Banned (true/false):");
        TextField banField = new TextField(existingUser != null ? String.valueOf(existingUser.isBanned()) : "false");

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10); grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(fnLabel, 0, 0); grid.add(fnField, 1, 0);
        grid.add(lnLabel, 0, 1); grid.add(lnField, 1, 1);
        grid.add(emailLabel, 0, 2); grid.add(emailField, 1, 2);
        grid.add(addrLabel, 0, 3); grid.add(addrField, 1, 3);
        grid.add(picLabel, 0, 4); grid.add(new HBox(5, picField, browseBtn), 1, 4);
        grid.add(roleLabel, 0, 5); grid.add(roleField, 1, 5);
        grid.add(banLabel, 0, 6); grid.add(banField, 1, 6);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                // Input validation
                fnField.getStyleClass().remove("input-error");
                lnField.getStyleClass().remove("input-error");
                emailField.getStyleClass().remove("input-error");
                addrField.getStyleClass().remove("input-error");
                roleField.getStyleClass().remove("input-error");
                banField.getStyleClass().remove("input-error");
                picField.getStyleClass().remove("input-error");

                StringBuilder errors = new StringBuilder();
                boolean hasError = false;

                if (fnField.getText().trim().isEmpty()) {
                    fnField.getStyleClass().add("input-error");
                    errors.append("- Le champ 'Nom' est requis.\n");
                    hasError = true;
                }

                if (lnField.getText().trim().isEmpty()) {
                    lnField.getStyleClass().add("input-error");
                    errors.append("- Le champ 'Prénom' est requis.\n");
                    hasError = true;
                }

                if (!emailField.getText().trim().matches("^(.+)@(.+)$")) {
                    emailField.getStyleClass().add("input-error");
                    errors.append("- Email invalide.\n");
                    hasError = true;
                }

                if (addrField.getText().trim().length() < 4) {
                    addrField.getStyleClass().add("input-error");
                    errors.append("- Adresse trop courte.\n");
                    hasError = true;
                }

                if (roleField.getText().trim().isEmpty()) {
                    roleField.getStyleClass().add("input-error");
                    errors.append("- Rôle manquant.\n");
                    hasError = true;
                }

                String banned = banField.getText().trim().toLowerCase();
                if (!banned.equals("true") && !banned.equals("false")) {
                    banField.getStyleClass().add("input-error");
                    errors.append("- Banned doit être 'true' ou 'false'.\n");
                    hasError = true;
                }

                String picPath = picField.getText().trim();
                if (!picPath.isEmpty()) {
                    File pic = new File(picPath);
                    if (!pic.exists() || pic.isDirectory()) {
                        picField.getStyleClass().add("input-error");
                        errors.append("- Fichier image invalide.\n");
                        hasError = true;
                    }
                }

                if (hasError) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreurs de saisie");
                    alert.setHeaderText("Veuillez corriger les erreurs suivantes :");
                    alert.setContentText(errors.toString());
                    alert.showAndWait();
                    return null;
                }

                return new User(
                        0,
                        fnField.getText().trim(),
                        lnField.getText().trim(),
                        emailField.getText().trim(),
                        "", // password not editable here
                        addrField.getText().trim(),
                        picField.getText().trim(),
                        roleField.getText().trim(),
                        Boolean.parseBoolean(banned)
                );
            }
            return null;
        });

        return dialog;
    }

    private void toggleUserRole(User user) {
        String newRole = user.getRoles().equalsIgnoreCase("admin") ? "user" : "admin";
        if (userService.updateUserRole(user.getId(), newRole)) {
            user.setRoles(newRole);
            refreshUserCards();
        }
    }

    @FXML
    public void updateUser() {
        User selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            selectedUser.setNom(firstNameField.getText());
            selectedUser.setPrenom(lastNameField.getText());
            selectedUser.setEmail(emailField.getText());
            selectedUser.setPassword(passwordField.getText());
            selectedUser.setAdresse(addressField.getText());
            selectedUser.setProfilePicture(profilePictureField.getText());
            selectedUser.setRoles(roleField.getText());
            selectedUser.setBanned(Boolean.parseBoolean(isBannedField.getText()));

            if (userService.updateUser(selectedUser)) {
                statusLabel.setText("User updated successfully.");
                userListView.refresh();
                clearFields();
            } else {
                statusLabel.setText("Failed to update user.");
            }
        }
    }

    @FXML
    public void deleteUser() {
        User selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            if (userService.deleteUser(selectedUser)) {
                statusLabel.setText("User deleted successfully.");
                users.remove(selectedUser);
            } else {
                statusLabel.setText("Failed to delete user.");
            }
        }
    }

    @FXML
    public void populateFields() {
        User selectedUser = userListView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            firstNameField.setText(selectedUser.getNom());
            lastNameField.setText(selectedUser.getPrenom());
            emailField.setText(selectedUser.getEmail());
            passwordField.setText(selectedUser.getPassword());
            addressField.setText(selectedUser.getAdresse());
            profilePictureField.setText(selectedUser.getProfilePicture());
            roleField.setText(selectedUser.getRoles());
            isBannedField.setText(String.valueOf(selectedUser.isBanned()));
        }
    }

    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        passwordField.clear();
        addressField.clear();
        profilePictureField.clear();
        roleField.clear();
        isBannedField.clear();
    }
}
