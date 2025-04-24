module org.example.gestionproduit {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires javafx.graphics;
    requires java.sql;
    requires spring.security.core;
    requires itextpdf;
    requires jdk.jsobject;
    requires javafx.web;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires stripe.java;
    requires java.desktop;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires java.mail;

    // Open the controller package so FXMLLoader can access it reflectively
    opens org.example.gestionproduit.controller to javafx.fxml;

    // Optionally export your main package if needed
    exports org.example.gestionproduit;
}
