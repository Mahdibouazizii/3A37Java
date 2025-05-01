module org.example.gestionproduit {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires javafx.graphics;
    requires java.sql;
    requires spring.security.core;
    requires itextpdf;
    requires com.google.zxing;  // ZXing core for QR codes
    requires com.google.zxing.javase;  // ZXing JavaSE for QR code support
    requires java.net.http;  // This gives access to the HttpClient, HttpRequest, etc.

    // Open the controller package for FXMLLoader reflection
    opens org.example.gestionproduit.controller to javafx.fxml;

    // Optionally export your main package if needed (for external access)
    exports org.example.gestionproduit;
}
