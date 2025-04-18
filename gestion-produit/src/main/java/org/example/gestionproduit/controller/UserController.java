package org.example.gestionproduit.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.example.gestionproduit.entity.User;
import org.example.gestionproduit.service.UserService;
import javafx.scene.Node;
import java.io.IOException;

public class UserController {

    private UserService userService = new UserService();

    @FXML
    private ListView<User> userListView;

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField profilePictureField;
    @FXML
    private TextField roleField;
    @FXML
    private TextField isBannedField;

    @FXML
    private Button addUserButton;
    @FXML
    private Button updateUserButton;
    @FXML
    private Button deleteUserButton;

    @FXML
    private Text statusLabel;

    // Observable List to bind the ListView
    private ObservableList<User> users;

    // Initialize method to populate the ListView with users
    public void initialize() {
        users = FXCollections.observableArrayList(userService.getAllUsers());

        // Set the cell factory to display the user's name
        userListView.setCellFactory(new Callback<ListView<User>, ListCell<User>>() {
            @Override
            public ListCell<User> call(ListView<User> param) {
                return new ListCell<User>() {
                    @Override
                    protected void updateItem(User item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            // Customize how each user is displayed in the ListView
                            setText(item.getNom() + " " + item.getPrenom() + " (" + item.getEmail() + ")");
                        } else {
                            setText(null);
                        }
                    }
                };
            }
        });

        // Bind the ListView to the users
        userListView.setItems(users);
    }

    // Add new user
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

    // Update existing user
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

    // Delete user
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

    // Populate fields when a user is selected
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

    // Clear input fields
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
