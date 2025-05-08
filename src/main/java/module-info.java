module org.example.gestionproduit {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires javafx.graphics;
    requires java.sql;
    requires spring.security.core;
    requires itextpdf;

    // Open the controller package so FXMLLoader can access it reflectively
    opens org.example.gestionproduit.controller to javafx.fxml;

    // Optionally export your main package if needed
    exports org.example.gestionproduit;
}
