package org.example.gestionproduit.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.gestionproduit.entity.Participation;
import org.example.gestionproduit.service.ServiceParticipation;

public class ModifierParticipationController {

    @FXML
    private TextField ageField;

    @FXML
    private TextField nbrPlaceField;

    @FXML
    private ComboBox<String> statutComboBox; // Remplace le TextField par ComboBox

    private Participation participation;

    public void setParticipationToUpdate(Participation p) {
        this.participation = p;
        ageField.setText(String.valueOf(p.getAge()));
        nbrPlaceField.setText(String.valueOf(p.getNbrplace()));
        statutComboBox.setValue(p.getStatut()); // Sélectionner le statut dans le ComboBox
    }

    @FXML
    public void initialize() {
        statutComboBox.getItems().addAll("premium", "en attente", "standard"); // Ajouter les options possibles
    }

    @FXML
    private void onSaveClicked() {
        try {
            participation.setAge(Integer.parseInt(ageField.getText()));
            participation.setNbrplace(Integer.parseInt(nbrPlaceField.getText()));
            participation.setStatut(statutComboBox.getValue()); // Utiliser la valeur sélectionnée dans le ComboBox

            ServiceParticipation service = new ServiceParticipation();
            service.modifier(participation); // À implémenter si pas encore fait

            // Fermer la fenêtre après modification
            ((Stage) ageField.getScene().getWindow()).close();

        } catch (NumberFormatException e) {
            e.printStackTrace();
            // Tu peux ajouter une alerte ici si les champs sont invalides
        }
    }
}
