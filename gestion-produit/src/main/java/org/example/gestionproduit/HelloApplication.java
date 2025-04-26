package org.example.gestionproduit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestionproduit/UserView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 900, 600); // ✅ Match AnchorPane size (600x400)

        // Load the CSS stylesheet
        scene.getStylesheets().add(getClass().getResource("/org/example/gestionproduit/style.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.setResizable(false); // Optional: prevent resizing
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
