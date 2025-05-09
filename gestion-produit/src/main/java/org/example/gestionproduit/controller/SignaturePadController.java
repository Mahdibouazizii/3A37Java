package org.example.gestionproduit.controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.example.gestionproduit.session.UserSession;
import org.example.gestionproduit.util.PathConstants;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

public class SignaturePadController {

    @FXML private Canvas  signatureCanvas;
    @FXML private Button  btnClear;
    @FXML private Button  btnSave;

    private GraphicsContext gc;

    @FXML
    public void initialize() {
        gc = signatureCanvas.getGraphicsContext2D();
        gc.setLineWidth(2);

        signatureCanvas.setOnMousePressed(e -> { gc.beginPath(); gc.moveTo(e.getX(), e.getY()); gc.stroke(); });
        signatureCanvas.setOnMouseDragged(e -> { gc.lineTo(e.getX(), e.getY());  gc.stroke(); });

        btnClear.setOnAction(e -> gc.clearRect(0,0,signatureCanvas.getWidth(),signatureCanvas.getHeight()));
        btnSave .setOnAction(e -> saveSignature());
    }

    private void saveSignature() {
        int userId = UserSession.getInstance().getUser().getId();

        /* ensure directory exists */
        File dir = new File(PathConstants.SIGNATURE_DIR);
        dir.mkdirs();

        File out = new File(dir, "signature_user_" + userId + ".png");
        try {
            BufferedImage img = SwingFXUtils.fromFXImage(signatureCanvas.snapshot(null,null), null);
            ImageIO.write(img, "png", out);
            System.out.println("✅ Signature saved to " + out.getAbsolutePath());
            closeWindow();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void closeWindow() {
        ((Stage) btnSave.getScene().getWindow()).close();
    }
}
